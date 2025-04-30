# ReentrantReadWriteLock的源码详细剖析

---

欢迎来到我的博客：[TWind的博客](http://www.twindworld.top/)

我的CSDN:：[Thanwind-CSDN博客](https://blog.csdn.net/qq_30004513?spm=1000.2115.3001.5343)

我的掘金：[Thanwinde 的个人主页](https://juejin.cn/user/634833993739484)

## 0.简介

ReentrantReadWriteLock，即读写锁，是JUC中一个极其重要的类，实现了读写的分离，极大的提高了并发量，源码延续了以往一贯的鬼斧神工，让人受益匪浅

建议先读《**ReentrantLock的详细源码剖析**》 以及 《**AQS条件队列源码详细剖析**》后再行阅读！

**注：本文适合想要深究读写锁的实现和原理，ReentrantLock的机制的人参考，文章较长**

---

## 1.读写锁

什么是读写锁？

数据库就是一个读写锁的集大成之作：读锁允许不同线程持有，而写锁只允许一个线程持有，也就是**读共享、写独占**

**因为读是不会发生线程安全问题的，那么完全可以读并发，写串行**

读写锁的逻辑就是这么简单，也和其他JUC的锁一样分为公平与非公平

+ 公平：只要同步队列不为空，读写锁就不会直接插队

+ 非公平：只要没有写锁在等待获得锁（处于队列第二位），读锁就会直接插队，但写锁可以一直插队(写优先)

如你所见，公平锁这种方式肯定会造成性能的损失，但取之而来的是不会发生**写锁饥饿**的情况

也就是大量的读锁一直插队导致写锁无法获得锁

同时，在高并发情况下**读写交替**会导致**公平锁**效率近似退化到可重入锁的水平

所以这两种锁到底采用哪一个方式得根据特定情况具体分析

### 锁降级/升级

锁降级指的是在已经持有写锁的情况下获取读锁

而锁升级指的是持有读锁的情况获得写锁，可能会导致死锁：比如ABC都有读锁，都尝试获得写锁，但写锁获取需要所有读锁释放：所以成了死锁

ReentrantReadWriteLock支持锁降级，不支持锁升级

---

## 2.ReentrantReadWriteLock源码

ReentrantReadWriteLock 整体的设计结构和 ReentrantLock 差不多，都是有一个Sync类实现主要功能，然后通过两个：NonfairSync 和 FairSync 来继承其，然后分别实现各种的公平/非公平的方法

所以，结构如下：

+ Sync :实现通用的方法，列如尝试获取，尝试释放等
+ NonfairSync ：非公平的实现
+ FairSync： 公平的实现
+ ReadLock：读锁
+ WriteLock：写锁

让我们先来看看创建一个ReentrantReadWriteLock对象会发生什么

---

### 构造函数

```java
public ReentrantReadWriteLock() {
    this(false);
}

public ReentrantReadWriteLock(boolean fair) {
    sync = fair ? new FairSync() : new NonfairSync();
    readerLock = new ReadLock(this);
    writerLock = new WriteLock(this);
}
```

如果未指定fair，默认是非公平，反之是公平的，并同时创建出读锁/写锁对象

---

接下来我们看一看Sync实现了哪些重要的方法

### Sync

首先开头有

```java
static final int SHARED_SHIFT   = 16;			
static final int SHARED_UNIT    = (1 << SHARED_SHIFT);//1左移16位，相当于1 * 2^16,为 65536，即0x0001 0000
static final int MAX_COUNT      = (1 << SHARED_SHIFT) - 1;	//即0x0000 FFFF
static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;	//0x0000 FFFF

/** Returns the number of shared holds represented in count  */
static int sharedCount(int c)    { return c >>> SHARED_SHIFT; }
/** Returns the number of exclusive holds represented in count  */
static int exclusiveCount(int c) { return c & EXCLUSIVE_MASK; }
```

这实际上是利用一个变量来同时存储一个线程的读锁/写锁数量，其实就是AQS中的state，在可重入锁，条件队列中都用来表示锁重入次数

由于在读写锁里面，需要同时记录读锁和写锁的重入次数，再开一个变量就显得复杂，于是就利用state分成了两部分：

+ 高四位存储 读锁重入次数 
+ 低四位存储 写锁重入次数

因为int类型由4个字节，8个16进制数组成组成，那就是0xFFFF 0000 FFFF就用来表示读锁，0000就来表示写锁

所以，读写锁的最大重入次数就为65535，即FFFF

sharedCount，exclusiveCount就是通过位运算来获取读锁，写锁的方法

---

#### readerShouldBlock / writerShouldBlock

```java
abstract boolean readerShouldBlock();

abstract boolean writerShouldBlock();
```

这两个都是抽象方法，供自定义实现

作用同方法名一样，用来判断是否阻止获取读锁，写锁插队的，这样就会让其去AQS排队

由公平/非公平模式决定，后面会讲解

---

#### tryRelease()

尝试释放一次写锁，这个方法是专门给写锁准备的

```java
protected final boolean tryRelease(int releases) {
    if (!isHeldExclusively())	//判断拿着写锁的线程是不是自己（也就是判断当前线程是不是独占模式，只能有一个线程有写锁）
        throw new IllegalMonitorStateException();
    int nextc = getState() - releases;//写锁是低4位，可以直接减
    boolean free = exclusiveCount(nextc) == 0;//通过exclusiveCount拿到写锁部分，判断是不是释放完了
    if (free)
        setExclusiveOwnerThread(null);	//是的话，就把拿着写锁的线程设成null
    setState(nextc);	//这里不用CAS，因为写锁是单线程的，这个方法也肯定是单线程
    return free;
}
```

先判断是不是独占模式（共享模式是读锁），是的话就操作state解锁，如果为0就直接释放掉锁

注意setState并不是CAS，因为只有一个线程能操作写锁，没有在上面的isHeldExclusively被挡下来就说明其是持有写锁的，只有他一个线程能操作

---

#### tryAcquire()

尝试获取一次写锁:

```java
protected final boolean tryAcquire(int acquires) {
    Thread current = Thread.currentThread();
    int c = getState();
    int w = exclusiveCount(c);	//获取锁计数和写锁计数
    if (c != 0) {	//计数不为0，说明有线程持有锁
        if (w == 0 || current != getExclusiveOwnerThread())//如果写锁为0，说明有线程在读，那就不能写
            return false;								//如果写锁不为0而且自己没有写锁，写锁只能有一个，不能写
        if (w + exclusiveCount(acquires) > MAX_COUNT)	//说明自己就有写锁，正常重入，超出就溢出
            throw new Error("Maximum lock count exceeded");
        // Reentrant acquire
        setState(c + acquires);//不用CAS，因为只会有一个线程有写锁
        return true;
    }
    if (writerShouldBlock() ||	//如果计数为0，说明没有线程有锁，那就CAS拿锁（可能有竞争)
        !compareAndSetState(c, c + acquires))
        return false;
    setExclusiveOwnerThread(current);
    return true;
}
```

这里先获得锁计数，然后开始判断：如果为0，说明即没有线程有锁，那就判断一下writerShouldBlock再CAS拿写锁

如果不为0，先看看写锁是不是0，是的话证明有读锁，有人在读就不能写，失败

反正，证明有人在写，而且不是自己，那更不能写了，失败

经过了上面的判断，说明这个线程本身有就写锁，那就锁重入，溢出就报错

---

#### tryReleaseShared()

尝试释放读锁：

```java
protected final boolean tryReleaseShared(int unused) {
    Thread current = Thread.currentThread();
    if (firstReader == current) {	//看看firstReader是不是本线程
        // assert firstReaderHoldCount > 0;
        if (firstReaderHoldCount == 1)	//如果为1，再释放就没了，直接设成空
            firstReader = null;
        else
            firstReaderHoldCount--;	//否则减1
    } else {
        HoldCounter rh = cachedHoldCounter;	
        if (rh == null || rh.tid != getThreadId(current))	//看看cachedHoldCounter是不是本线程
            rh = readHolds.get();						//不是的话就设成本线程，readHolds是ThreadLocal类型，保存每一个线程
        int count = rh.count;							//自己的读锁数
        if (count <= 1) {
            readHolds.remove();						//如果释放完了就直接移除
            if (count <= 0)
                throw unmatchedUnlockException();
        }
        --rh.count;
    }
    for (;;) {			//不断CAS来修改锁的重入次数
        int c = getState();
        int nextc = c - SHARED_UNIT;
        if (compareAndSetState(c, nextc))
            return nextc == 0;
    }
}
```

这里注意，每个线程都会维护自己的读锁次数，在for循环上面的代码就是这个功能的代码，下面详细解释：

readHolds是一个继承了ThreadLocal<HoldCounter>的ThreadLocal变量，存储每一个线程自己的锁数量

get，set方法是操作ThreadLocalMap，线程的锁个数就放在其中

这里的“计数”是存储在每个线程的ThreadLocal里面的，每次都要去定位然后修改会造成性能损失

这里就采用了两个优化：

+ firstReader指向第一个读线程，只是一个普通的int来代替第一个线程的ThreadLocal变量，避免每次都要定位造成损失。firstReader在这个线程的读锁释放完之前并不会更改

+ cachedHoldCounter 代表最近一个拿到读锁的线程，其指向其ThreadLocal，以后如果还是它来拿锁就能直接操作其ThreadLocal变量，不用去定位

在只有一个线程，或活跃线程的情况下，能很大程度上加速这个过程

但是，为什么需要每个线程都保存自己的读锁次数呢？

比如现在有ABC三个线程都拿到了读锁

现在A连着执行了三次tryReleaseShared（），直接把state清零了，但是B,C的读锁都还没有释放呢！

这就是要维持这一个“副本”的原因

---

#### tryAcquireShared()

```java
protected final int tryAcquireShared(int unused) {
    Thread current = Thread.currentThread();
    int c = getState();	//获取锁计数
    if (exclusiveCount(c) != 0 &&
        getExclusiveOwnerThread() != current)	//如果已经有人拿到了写锁而且不是自己，那就失败
        return -1;
    int r = sharedCount(c);		//获取读锁个数
    if (!readerShouldBlock() &&
        r < MAX_COUNT &&
        compareAndSetState(c, c + SHARED_UNIT)) {		//如果不违反readerShouldBlock而且CAS读锁数成功且不溢出
        if (r == 0) {
            firstReader = current;
            firstReaderHoldCount = 1;			//如果自己是第一个读锁，就把自己设成firstReader来加速
        } else if (firstReader == current) {
            firstReaderHoldCount++;				//如果自己是firstReader，就通过firstReader来+1读锁数
        } else {
            HoldCounter rh = cachedHoldCounter;	//不是firstReader，就看看自己是不是cachedHoldCounter
            if (rh == null || rh.tid != getThreadId(current))
                cachedHoldCounter = rh = readHolds.get();	//不是的话就把自己设成新的cachedHoldCounter
            else if (rh.count == 0)	//执行到这说明缓存命中了，如果这里是0，说明上一次已经把锁全部释放了，已经remove了
                readHolds.set(rh);	//就得重新把引用加回去
            rh.count++;	//锁计数+1（也就是readHolds里面的计数+1）
        }
        return 1;//正常的执行结果
    }
    return fullTryAcquireShared(current);//如果执行到这里，说明CAS失败/readerShouldBlocks失败/r < MAX_COUNT
}
```

这里会尝试获取读锁，只能获取一次，所以你看传入的变量名为 “unused”

首先会尝试获取锁计数，检查写锁是不是0，不是的话检测是不是自己的写锁，还不是的话就直接失败：有写锁时不能再获取读锁

然后会获取读锁的个数，检测readerShouldBlock以及不会溢出后直接CAS设置锁：读锁可以并存

然后就是设置线程的锁个数变量了：

和上面的tryReleaseShared，先检测firstReader，如果r为空说明自己是第一个读线程，就成为firstReader，替换成firstReaderHoldCount++

如果不是的话，就看看自己是不是上一个线程，是的话就就直接通过rh.count++来加计数：HoldCounter rh = cachedHoldCounter，

rh 是对其threadlocal变量的引用

注意：如果这时检测到rh.count == 0，说明这个线程上一次调用把锁清零了，参考tryReleaseShared，清零会调用remove，清除readHolds里面的引用，所以这里要重新设置一下引用！

如果没问题的话会返回1，但是如果CAS失败/readerShouldBlocks失败/r < MAX_COUNT，就会转到fullTryAcquireShared：

---

#### fullTryAcquireShared()

这里是处理上述问题的代码

```java
final int fullTryAcquireShared(Thread current) {
    HoldCounter rh = null;
    for (;;) {
        int c = getState();	//获取锁记录
        if (exclusiveCount(c) != 0) {
            if (getExclusiveOwnerThread() != current)
                return -1;	//同上，有写锁且不是自己的就直接失败
        } else if (readerShouldBlock()) {	//如果这里readerShouldBlock为真，就会执行firstReader == current
            if (firstReader == current) {
                						//接着再判断rh.count，来知道这个线程是不是拿过锁，拿过就放行
            } else {
                if (rh == null) {
                    rh = cachedHoldCounter;
                    if (rh == null || rh.tid != getThreadId(current)) {
                        rh = readHolds.get();
                        if (rh.count == 0)
                            readHolds.remove();
                    }
                }
                if (rh.count == 0)
                    return -1;		//没有锁，说明是新线程，失败
            }
        }
        										//下面的代码和之前的更新操作几乎一模一样，不再赘述
        if (sharedCount(c) == MAX_COUNT)
            throw new Error("Maximum lock count exceeded");
        if (compareAndSetState(c, c + SHARED_UNIT)) {
            if (sharedCount(c) == 0) {
                firstReader = current;
                firstReaderHoldCount = 1;
            } else if (firstReader == current) {
                firstReaderHoldCount++;
            } else {
                if (rh == null)
                    rh = cachedHoldCounter;
                if (rh == null || rh.tid != getThreadId(current))
                    rh = readHolds.get();
                else if (rh.count == 0)
                    readHolds.set(rh);
                rh.count++;
                cachedHoldCounter = rh; // cache for release
            }
            return 1;
        }
    }
}
```

这个代码会处理tryAcquireShared出错的情况

首先会判断是不是有写锁，是不是自己的

接着判断readerShouldBlock，如果是真的，再判断

if (firstReader == current)，如果为真，证明该线程拿过锁，而且还有读锁，应该让其继续拿锁（如果读锁被释放完了firstReader 会被清除）

或者拿到rh.count，看看当前线程有没有读锁，效果同上，如果有锁说明是重入，直接放行

反正，就会返回-1失败

接下来能执行的话，证明这个线程是由资格拿到读锁的，就不断循环CAS尝试了

---

到这里，Sync的方法已经全部讲解完成，你可能还会存在一些疑惑，但等到读完下面的内容后相信你会豁然开朗







---

## NonfairSync 

这个类继承了Sync，是非公平版本，代码非常简单：

```java
static final class NonfairSync extends Sync {
    private static final long serialVersionUID = -8159625535654395037L;
    final boolean writerShouldBlock() {
        return false; 
    }
    final boolean readerShouldBlock() {

        return apparentlyFirstQueuedIsExclusive();
    }
}
```

对于非公平的writerShouldBlock，直接返回了false，说明任何情况下，写锁都能插队

而对于readerShouldBlock，返回的是apparentlyFirstQueuedIsExclusive：

```java
final boolean apparentlyFirstQueuedIsExclusive() {
    Node h, s;
    return (h = head) != null &&
        (s = h.next)  != null &&
        !s.isShared()         &&
        s.thread != null;
}
```

这里是只有第二个节点，也就是马上获得锁的节点是写节点（isShared（））才会返回真，其余条件都是false

也就是只要不是有一个写锁节点在等待，就会允许插队

> [!NOTE]
>
> 这里说明一下，readerShouldBlock，writerShouldBlock是怎么控制插队的
>
> 我们可以看到，在Sync中，没有任何涉及到加入AQS同步队列的代码
>
> 在ReadLock，WriteLock的lock方法中，会先尝试用Sync中的方法快速获取一次锁，如果这次快速操作失败了，就会将其加入同步队列之中
>
> 而readerShouldBlock，writerShouldBlock如果返回true的话，就会让除了已经重入的线程失败，那么这些都会被加入到AQS队列之中

---

## FairSync

```java
static final class FairSync extends Sync {
    private static final long serialVersionUID = -2274990926593161451L;
    final boolean writerShouldBlock() {
        return hasQueuedPredecessors();
    }
    final boolean readerShouldBlock() {
        return hasQueuedPredecessors();
    }
}
```

FairSync更为简单：只要同步队列中有节点就会直接失败:

```java
public final boolean hasQueuedPredecessors() {
    Node t = tail; 
    Node h = head;
    Node s;
    return h != t &&
        ((s = h.next) == null || s.thread != Thread.currentThread());
}
```

这意味着，其不会造成写锁饥饿（因为大家都去排队了，读锁不能冲到写锁前面）

而代价就是，只有在线程较少的情况下有优异的性能，一旦读写相织，就会一样退化到可重入锁串行的水平，因为一直有节点就会一直排队

---

## ReadLock

这个就是重中之重：读锁

这个类以及WriteLock里面的api就是直接对用户暴露的，是读写锁的核心

### 构造函数

```java
protected ReadLock(ReentrantReadWriteLock lock) {
    sync = lock.sync;
}
```

这里会根据公平/非公平模式来决定其Sync模式，其实也就是更改writerShouldBlock和readerShouldBlock

---

### lock()

```java
public void lock() {
    sync.acquireShared(1);
}
```

万物起源，让我们跟入：

```java
public final void acquireShared(int arg) {
    if (tryAcquireShared(arg) < 0)	//快速尝试一次
        doAcquireShared(arg);	//如果失败就进入队列
}
```

这里面已经进入了AbstractQueuedSynchronizer（AQS）类中了

这里先尝试用tryAcquireShared试一次，也就是我之前提到的“快速尝试”，如果被readerShouldBlock阻止或者有写锁存在之类的就会失败，那么就会进入

doAcquireShared之中：

```java
private void doAcquireShared(int arg) {
    final Node node = addWaiter(Node.SHARED);	//构造节点并把节点加入到队列尾部，节点状态为SHARED
    boolean failed = true;
    try {
        boolean interrupted = false;	//中断标记初始为false
        for (;;) {
            final Node p = node.predecessor();
            if (p == head) {	//只有当前驱为头节点时，才能获取锁成功，从循环中出来
                int r = tryAcquireShared(arg);
                if (r >= 0) {		//r>=0就代表成功通过tryAcquireShared获取锁
                    setHeadAndPropagate(node, r);	//传播
                    p.next = null; // help GC
                    if (interrupted)
                        selfInterrupt();	//如果中途发生过中断，就手动中断一下，防止interrupted吞掉中断标志
                    failed = false;
                    return;
                }
            }
            if (shouldParkAfterFailedAcquire(p, node) &&
                parkAndCheckInterrupt())	//这里会让节点处理好后事后park自己
                interrupted = true;
        }
    } finally {
        if (failed)
            cancelAcquire(node);	//如果出现了异常就取消自己
    }
}
```

如果看过条件队列以及可重入锁的人对这个代码一定非常熟悉

这里不再对此类型代码赘述，可以看我的《**ReentrantLock的详细源码剖析**》，里面详细的对这类代码进行了剖析与解释

这里我只解释一下setHeadAndPropagate方法，这是唯一不同的方法，也是整个共享锁机制的**重中之重**

```java
private void setHeadAndPropagate(Node node, int propagate) {
    Node h = head; 
    setHead(node);	//把老头存在h之中，并把自己设成新头

    if (propagate > 0 || h == null || h.waitStatus < 0 ||
        (h = head) == null || h.waitStatus < 0) {	//这里会进行一系列条件的判断，只要有一个成立就会进入下面
        Node s = node.next;
        if (s == null || s.isShared())	//如果接下来没有后继或者后继是共享节点
            doReleaseShared();			//就会进入doReleaseShared，尝试唤醒后继线程
    }
}
```

首先，会把自己设成头节点，然后，看看有没有满足下面的各个条件：

+ propagate > 0 ：规定>0就会继续传播,= 0  不必强制传播 ,读写锁固定是1，就是会一直传播，这里涉及到"资源"的概念，在ReentrantReadWriteLock中并没有用，因为propagate 固定为1，简单来说就是衡量有没有“资源”来再支持共享读，这里不作展开，详细会在后面的StampLock中详细讲解
+ h == null                     老头为空（说明我们是第一个入队）
+ h.waitStatus < 0              老头是SIGNAL或者PROPAGATE
+ (h = head) == null            并发修改后，head 被其他线程清空
+ h.waitStatus < 0) {          新头也标着 SIGNAL / PROPAGATE

满足这些后，就会去判断，下一个节点是不是空或者是共享节点，是的话就会执行doReleaseShared（）唤醒

```java
private void doReleaseShared() {

    for (;;) {
        Node h = head;
        if (h != null && h != tail) {
            int ws = h.waitStatus;
            if (ws == Node.SIGNAL) {
                if (!compareAndSetWaitStatus(h, Node.SIGNAL, 0))
                    continue;            // loop to recheck cases
                unparkSuccessor(h);
            }
            else if (ws == 0 &&
                     !compareAndSetWaitStatus(h, 0, Node.PROPAGATE))
                continue;                // loop on failed CAS
        }
        if (h == head)                   // loop if head changed
            break;
    }
}
```

这里是一个很关键的点，如果自己是SIGNAL就改成0来并唤醒下一个线程很容易弄懂，但是为什么如果自己是0的话就要设成PROPAGATE而不唤醒呢？

这里我们要再次重申一下AQS同步队列里面的“契约”：当一个节点是SIGNAL（-1），就会在自己释放锁后唤醒下一个线程

如果是0的话，就不会做任何操作

**现在我们我们假定没有PROPAGATE这个状态，而且资源（propagate）不是固定为1**

首先，线程1进入了队列，成为了头节点，而且没有后继节点加入，这意味着其waitState为0，**signal状态是由后继节点赋予的**

执行setHeadAndPropagate：

```java
if (propagate > 0 || h == null || h.waitStatus < 0 ||
        (h = head) == null || h.waitStatus < 0)
```

这里如果propagate为正，就会进入，我们假设此时有资源已经进入了

接下来执行doReleaseShared：

因为没有if (ws == 0 &&
                     !compareAndSetWaitStatus(h, 0, Node.PROPAGATE))

所以就直接退出，保持原有的waitState = 0，不做任何操作

此时，出现了一个线程2在尝试获得读锁

它首先会

```java
public void lock() {
    sync.acquireShared(1);
}
```

然后

```java
public final void acquireShared(int arg) {
    if (tryAcquireShared(arg) < 0)
        doAcquireShared(arg);
}
```

我们假设它tryAcquireShared失败了，由于CAS或者readerShouldBlock等等

然后它会进入AQS的逻辑之中：

```java
private void doAcquireShared(int arg) {
    final Node node = addWaiter(Node.SHARED);	//构造节点并把节点加入到队列尾部，节点状态为SHARED
    boolean failed = true;
    try {
        boolean interrupted = false;	//中断标记初始为false
        for (;;) {
            final Node p = node.predecessor();
            if (p == head) {	//只有当前驱为头节点时，才能获取锁成功，从循环中出来
                int r = tryAcquireShared(arg);
                if (r >= 0) {		//r>=0就代表成功通过tryAcquireShared获取锁
                    setHeadAndPropagate(node, r);	//传播
                    p.next = null; // help GC
                    if (interrupted)
                        selfInterrupt();	//如果中途发生过中断，就手动中断一下，防止interrupted吞掉中断标志
                    failed = false;
                    return;
                }
            }
            if (shouldParkAfterFailedAcquire(p, node) &&
                parkAndCheckInterrupt())	//这里会让节点处理好后事后park自己
                interrupted = true;
        }
    } finally {
        if (failed)
            cancelAcquire(node);	//如果出现了异常就取消自己
    }
}
```

> [!WARNING]
>
> 注意！如果进来的新节点一下就成功，没有park的话，是不会修改前驱节点的状态的！
>
> 修改状态的代码位于shouldParkAfterFailedAcquire之中，如果这个节点在第一次就拿到了锁，就不会park
>
> 同样的，也不会修改前驱节点的waitState ，这意味着，**老头节点的waitState 仍然是0**
>
> 那么，这时候我们再来看看其执行setHeadAndPropagate的操作：
>
> if (propagate > 0 || h == null || h.waitStatus < 0 ||
>         (h = head) == null || h.waitStatus < 0)
>
> 如果这时候没资源了，而且由于自身背后，头节点waitState都是0，所有条件都不会满足！
>
> 这时候如果来了一大堆共享节点连在了新头之后，虽然新头改成了SIGNAL，新头也不会执行doReleaseShared将他们提前唤醒
>
> 这就导致直到新头完全释放锁之前，这些共享线程完全不会获得锁
>
> 这个bug存在于JDK6/7,这也是PROPAGATE引入的原因，bug编号为 [6801020](https://bugs.openjdk.org/browse/JDK-6801020)

到了这里，相信你对共享锁会有一个全新的认识，如果看不懂没关系，后面介绍StampLock的文章会还会详细解析

---

### unlock()

相比起lock，unlock就显得很简单：

```java
public void unlock() {
    sync.releaseShared(1);
}
```

```java
public final boolean releaseShared(int arg) {
    if (tryReleaseShared(arg)) {
        doReleaseShared();
        return true;
    }
    return false;
}
```

先会用tryReleaseShared释放锁，看看有没有释放完，释放完了就调用doReleaseShared唤醒后继线程

doReleaseShared就是上面的那个，获得锁，释放锁都会尝试唤醒后继线程，这是共享锁的特性

---

### lockInterruptibly（）

```java
public void lockInterruptibly() throws InterruptedException {
    sync.acquireSharedInterruptibly(1);
}
```

没啥好讲的，就是单纯的遇到中断会抛出异常

```java
public final void acquireSharedInterruptibly(int arg)
        throws InterruptedException {
    if (Thread.interrupted())
        throw new InterruptedException();
    if (tryAcquireShared(arg) < 0)
        doAcquireSharedInterruptibly(arg);
}
```

```java
private void doAcquireSharedInterruptibly(int arg)
    throws InterruptedException {
    final Node node = addWaiter(Node.SHARED);
    boolean failed = true;
    try {
        for (;;) {
            final Node p = node.predecessor();
            if (p == head) {
                int r = tryAcquireShared(arg);
                if (r >= 0) {
                    setHeadAndPropagate(node, r);
                    p.next = null; // help GC
                    failed = false;
                    return;
                }
            }
            if (shouldParkAfterFailedAcquire(p, node) &&
                parkAndCheckInterrupt())
                throw new InterruptedException();	//原本的设置中断取而代之成了直接抛出异常
        }
    } finally {
        if (failed)
            cancelAcquire(node);
    }
}
```

原本的设置中断取而代之成了直接抛出异常

---

### tryLock(long timeout, TimeUnit unit)

超时锁：

```java
public boolean tryLock(long timeout, TimeUnit unit)
        throws InterruptedException {
    return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
}
```

同样是会抛出异常的

```java
public final boolean tryAcquireSharedNanos(int arg, long nanosTimeout)
        throws InterruptedException {
    if (Thread.interrupted())
        throw new InterruptedException();
    return tryAcquireShared(arg) >= 0 ||
        doAcquireSharedNanos(arg, nanosTimeout);
}
```

```java
private boolean doAcquireSharedNanos(int arg, long nanosTimeout)
        throws InterruptedException {
    if (nanosTimeout <= 0L)
        return false;
    final long deadline = System.nanoTime() + nanosTimeout;	//设置超时时间
    final Node node = addWaiter(Node.SHARED);
    boolean failed = true;
    try {
        for (;;) {
            final Node p = node.predecessor();
            if (p == head) {
                int r = tryAcquireShared(arg);
                if (r >= 0) {
                    setHeadAndPropagate(node, r);
                    p.next = null; // help GC
                    failed = false;
                    return true;
                }
            }
            nanosTimeout = deadline - System.nanoTime();	//看看超时没有
            if (nanosTimeout <= 0L)	
                return false;		//如果超时就直接失败
            if (shouldParkAfterFailedAcquire(p, node) &&
                nanosTimeout > spinForTimeoutThreshold)
                LockSupport.parkNanos(this, nanosTimeout);
            if (Thread.interrupted())	
                throw new InterruptedException();	//直接抛出异常
        }
    } finally {
        if (failed)
            cancelAcquire(node);
    }
}
```

---

### newCondition()

```java
public Condition newCondition() {
    throw new UnsupportedOperationException();
}
```

可以看到，读锁不支持condition,相对应的，写锁就支持了condition，因为实际上写锁就是可重入锁，没有什么区别

---

## WriteLock

写锁相对读锁来说，可是说是非常之简单了

其实和可重入锁并没有什么区别，就一个可重入非公平锁

所以解析会相对少一些，想要了解的话可以看我的博客的《**ReentrantLock的详细源码剖析**》

---

### 构造函数

```java
protected WriteLock(ReentrantReadWriteLock lock) {
    sync = lock.sync;
}
```

根据你的Sync决定是公平还是非公平

### lock()

```java
public void lock() {
    sync.acquire(1);
}
```

```java
public final void acquire(int arg) {
    if (!tryAcquire(arg) &&
        acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
        selfInterrupt();
}
```

熟不熟悉？其实这就是ReentrantLock里面的lock，完全没变

```java
final boolean acquireQueued(final Node node, int arg) {
    boolean failed = true;
    try {
        boolean interrupted = false;
        for (;;) {
            final Node p = node.predecessor();
            if (p == head && tryAcquire(arg)) {
                setHead(node);
                p.next = null; // help GC
                failed = false;
                return interrupted;
            }
            if (shouldParkAfterFailedAcquire(p, node) &&
                parkAndCheckInterrupt())
                interrupted = true;
        }
    } finally {
        if (failed)
            cancelAcquire(node);
    }
}
```

想看解释的话请移步《**ReentrantLock的详细源码剖析**》，代码完全一样

---

### lockInterruptibly()

会抛出中断的lock，也是一点都没有变，这里就不再赘述

```java
public void lockInterruptibly() throws InterruptedException {
    sync.acquireInterruptibly(1);
}
```

---

### tryLock(long timeout, TimeUnit unit)

```java
public boolean tryLock(long timeout, TimeUnit unit)
        throws InterruptedException {
    return sync.tryAcquireNanos(1, unit.toNanos(timeout));
}
```

超时锁，不再赘述

---

### newCondition（）

```java
public Condition newCondition() {
    return sync.newCondition();
}
```

写锁其实就是ReentrantLock里面的非公平锁，那当然支持条件队列，条件队列可以看我的《**AQS条件队列源码详细剖析**》

---

---

---

# 3.总结

读写锁到这里就结束了，总的来说，单单根据一个读写锁并不能展现共享锁的全部。

共享锁最重要的资源与限制在读写锁中都没有出现

但无论如何，从读写锁来学习共享锁仍然非常不错

读锁相对复杂，写锁就是ReentrantLock的源码

后面会写一篇解析StampLock的文章，欢迎捧场！


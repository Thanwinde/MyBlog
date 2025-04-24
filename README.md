# 一个自用的简单博客框架
---
示例：www.twindworld.com

由springWeb搭建后端，o3提供前端

由md文件导出html实现博客上传，因此支持热更新

使用者只需要将yml中存放的blog文件路径和index文件路径改成自己的即可，教程在下面

获取评论的接口为/api/getComment

获取博客列表为/api/getBlogs

注册，登录同理，不再赘述

getBlogs会返回一个json，里面包括了分类，该分类的博客name，可以供前端拼接出链接

评论采用了简单的mysql数据库存储

具体来说：

对于博客存储目录为：

---blogs---技术---5mm入门到精通.html

|		 |

|		 闲聊---cy闲谈.html

|

|---index.html

gift文件夹里面有实例文件（以及index.html等，按需取用）

要设置网站图标的话修改favicon.ico即可

然后在yml中改成**blogs的路径以及index.html的上一级的路径**

程序会递归扫描其中每一个文件夹作为分类名，并将其中的html文件截取文件名封装返回前端

可以参照源代码帮助理解

评论需登录后才可发表，会同时显示昵称和邮箱

注册采用邮箱发验证邮件注册，一个邮箱只能注册一个账户

---


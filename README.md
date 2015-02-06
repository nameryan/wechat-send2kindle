# wechat-send2kindle
this is a wechat project which can send contents to kindle devices from wechat

-----------------------------------------------------------------------------------------

wechat-send2kindle 是一个基于BAE平台的微信公众号项目，公众号的目的是推送wechat上的精彩文章到您的kindle设备，支持kindle原生系统和多看系统。

公众号账号：发送到Kindle （目前已不维护）

目前的代码支持抓取微信文章的主体内容部分，将文章内容以txt文件的格式通过email发送到事先绑定的kindle账号或者iduokan账号。
wechat代码部分实现了基础的开发者模式框架，可以实现简单的自动回复，数据库记录等。
由于是基于BAE的平台，因此里面用了一些BAE的接口，比如插入数据库等，当需要迁移至其他平台时，这些接口需要更改。
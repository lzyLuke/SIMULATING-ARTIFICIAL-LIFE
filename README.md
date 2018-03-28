# SIMULATING ARTIFICIAL LIFE



------

这是我在Cornell里的CS2112 Object Oriented Programming and Data Structures for Honors课上做的一个项目,呃,是一个比较好玩的东西，但我很难把它描述成一个实际的东西。。。但想了想，应该可以说是一个游戏吧。总单来说就是在一个自己构建的地图上放置可以用自己定义的语言编程的机器人一样的模拟器。。。

## 1 图形化界面基本样式

图形化界面是使用JAVA SceneBuilder构建的。

右边的左Panel，用来显示当前格子（鼠标可选择的）生物的状态，包括它的基因，目前各项数值，上一步执行的哪一条语句：
![1](https://github.com/lzyLuke/SIMULATING-ARTIFICIAL-LIFE/blob/master/pics/1.png)

右边的中Panel，用来显示当前格子是石头还是食物etc....
![2](https://github.com/lzyLuke/SIMULATING-ARTIFICIAL-LIFE/blob/master/pics/2.png)

右边的右Panel，用来显示一些配置，可以自己读入一个新世界，随机生成世界，读入一个生物文件，放置一个生物，调整世界流逝时间速度等...
![3](https://github.com/lzyLuke/SIMULATING-ARTIFICIAL-LIFE/blob/master/pics/3.png)

右上方的+，-号可以用来动态调整格子的大小（同时会重新绘制整个画面）。
![4](https://github.com/lzyLuke/SIMULATING-ARTIFICIAL-LIFE/blob/master/pics/4.png)
![5](https://github.com/lzyLuke/SIMULATING-ARTIFICIAL-LIFE/blob/master/pics/5.png)

## 2 生物部分描述（程序语言）

程序语言语法树如下：
![syntaxTree](https://github.com/lzyLuke/SIMULATING-ARTIFICIAL-LIFE/blob/master/pics/syntaxTree.png)





样例生物基因表达为：
```
POSTURE != 17 --> POSTURE := 17; // we are species 17! nearby[3] = 0 and ENERGY > 2500 --> bud;
{ENERGY > SIZE * 400 and SIZE < 7} --> grow;
ahead[0] < -1 and ENERGY < 500 * SIZE --> eat;
// next line attacks only other species
(ahead[1] / 10 mod 100) != 17 and ahead[1] > 0 --> attack;
ahead[1] < -5 --> forward;
{ahead[2] < -10 or random[20] = 0} and ahead[1] = 0 --> forward;
ahead[3] < -15 and ahead[1] = 0 --> forward;
ahead[4] < -20 and ahead[1] = 0 --> forward;
nearby[0] > 0 and nearby[3] = 0 --> backward;
// karma action: donate food if we are too full or large enough ahead[1] < -1 and { ENERGY > 2500 or SIZE > 7 } --> serve[ENERGY / 42]; random[3] = 1 --> left;
1 = 1 --> wait; // mostly soak up the rays
```



生物的语言主要是满足左边condition条件就会执行右边的语句。
生物在世界的一个时间单位只执行一条条件为真的语句（如果有真的话）


除了生物本身可以根据当前环境（其能感受到部分周围的格子的信息）进行本身的转向或移动以外，还能进行对其他生物的攻击，自身的防御。值得一提的是，生物可以进行繁殖，是两个同样种类面对面进行，如若繁殖成功，子代首先会各取父母一半的基因（一半的程序），然后会有一定的概率发生自身基因的变异（Mutation），变异的过程是首先读入基因，构造出语法树，然后随机替换语法树上的某些节点成某些语法子树或另外的叶子节点。这样就模拟了生物的基因变异。


## 3 分布式C/S服务器

除了编译器这一部分以外，另一个比较Challenge的地方是分布式实现，实现一个C/S模型，把程序分为服务器端和客户端。

网络方面是用[SparkJava](http://sparkjava.com/"SparkJava")框架进行HTTP的POST和GET。服务器端负责所有的计算以及所有后台操作，客户端则只有图形化界面的显示和根据权限被允许上传生物，上传地图或者看其他生物的基因等功能。

客户端在第一次链接服务器的时候会返回一个登陆的SessionId，以及当前服务器所计算出的最新的所有地图。往后的客户端就用这个唯一标识的SessionID来进行对话，服务器会根据这个SessionId来识别用户，判断用户的权限等。

其中最难的点是如何根据不同地图的版本号传送之间的差异。我们规定，每当地图上有变化——生物移动了，生物自身属性发生了变化等，地图的版本号就会增加一个，每一个增加的版本号服务器就会记录那个版本号的地图（这个方法虽然比较笨但还是比较有效的）。为了在记录地图不与其他线程运行的相冲突，我们使用了JAVA的ReentrantLock对地图的每一个格子进行了上锁，对于读地图的行为，上读锁，对于要更改地图的行为，上写锁，对于一些并发的数据结构，使用了Concurrant包里面的ConcurrantHashMap和Collections.synchronizedList。

每当客户端请求更新，并附带自身的版本号，服务器端会计算出本身最新版本号与客户端的版本号之间的差异，并返回其中的差异List（不会返回整个最新的地图，仅仅会返回需要更新的部分。）


------

If you have any questions，please sent an email to zeyu.luke@gmail.com
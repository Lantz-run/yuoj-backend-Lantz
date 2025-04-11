## 后端项目初始化

1）先下载一份一份 springbootinit 万用模板（已经在本地的话直接复制使用）

2）`ctrl + shift + R` 全局替换 springboot-init 为项目名（yuoj-backend）

3）全局替换 springbootinit 包名为新的包名（yuoj）

4）修改 springbootinit 文件夹的名称为新的包名对应的名称（yuoj）

5）改 application.yml 配置，修改 MySQL 数据库的连接库名、账号密码、修改端口号（8121）

6）本地新建数据库，直接执行 sql/create_table.sql 脚本，修改库名为 yuoj	 

错误：如果再次登录显示用户不存在或者密码错误，就在 userRegister 再次注册该账号



## 初始化模板讲解

1）先阅读 README.md

2）sql/create_table.sql 定义了数据库的初始化建库建表语句

3）sql/post_es_mapping.json 帖子表在 ES 中的建表语句

4）aop：用于全局权限校验、全局日志记录

5）common：万用的类，比如通用响应类

6）config：用于接收 application.yml 中的参数，初始化一些客户端的配置类（比如对象存储客户端）

7）constant：定义常量

8）constroller：接受请求

9）esdao：类似 mybatis 的mapper，用于操作 ES 

10）exception：异常处理相关

11）job：任务相关（定时任务、单次任务）

12）manager：服务层（一般是定义一些公用的服务、对接第三方 API 等）

13）mapper：mybatis 的数据访问层、用于操作系统等

14）model：数据模型、实体类、包装类、枚举值

15）service：服务层，用于编写业务逻辑

16）utils：工具类，各种各样公用的方法

17）wxmp：公众号相关的包

18）test：单元测试

19）MainApplication：项目启动入口

20）Dockerfile：用于构建 Docker 镜像





## 题目表

题目标题

题目内容：存放题目的介绍、输入输出提示、描述、具体的详情

题目的标签（ json 数组字符串）：栈、队列、链表、简单、中等、困难

题目答案：管理员 / 用户设置的标准答案

提交数、通过人数统计等：便于分析统计（可以根据通过率自动给题目打标签）



判题相关字段：

如果说题目不是很复杂，用例文件大小不大的话，可以直接存放数据库表里

但是如果用例文件比较大， > 512 KB 建议单独存放一个文件中，数据库中只存放文件 url （类似存储用户头像）



- 输入用例：1、2
- 输出用例：3、4
- 时间限制
- 内存限制



judgeConfig 判题配置（json 对象）：

- 时间限制 timeLimit
- 内存限制 memoryLimit

judgeCase 判题用例（json数组）

- 每一个元素是：一个输入用例对应一个输出用例

- ```
  [
   {
   	"input": "1 2",
   	"output": "3 4"
   },
   {
   	"input": "1 3",
   	"output": "2 4"
   }
  ]
  ```

  





存 json 的好处：便于扩展，只需要改变对象内部的字段，而不用修改数据库（可能会影响数据库）

```json
{

    "timeLimit": 1000,
    "memoryLimit": 1000,
    "stackLimit": 1000

}
```



存 json 的前提

1. 你不需要根据某个字段去倒查这条数据
2. 你的字段含义相关，属于同一类的值
3. 你的字段存储空间占用不能太大



其他扩展字段：

- 通过率（已实现）

  - ```java
    // 获取判题结果信息
    String message = judgeInfo.getMessage();
    questionSubmitUpdate = new QuestionSubmit();
    questionSubmitUpdate.setId(questionSubmitId);
    
    // 6）修改数据库中的判题结果
    if ("Accepted".equals(message)) {
        acceptedNum++;
        question.setAcceptedNum(acceptedNum);
        submitNum++;
        question.setSubmitNum(submitNum);
        questionFeignClient.updateQuestion(question);
        // 直接使用已存在的 questionSubmitUpdate 对象
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
    } else {
        submitNum++;
        question.setSubmitNum(submitNum);
        questionFeignClient.updateQuestion(question);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
    }
    ```

- 判题类型

代码：

```mysql
-- 题目表
create table if not exists question
(
    id          bigint auto_increment comment 'id' primary key,
    title       varchar(512)                       null comment '标题',
    content     text                               null comment '内容',
    tags        varchar(1024)                      null comment '标签列表（json 数组）',
    answer      text                               null comment '题目答案',
    submitNum   int      default 0                 not null comment '题目提交数',
    acceptedNum int      default 0                 not null comment '题目通过数',
    judgeCase   text                               null comment '判题用例（json 数组）',
    judgeConfig text                               null comment '判题配置（json 对象）',
    thumbNum    int      default 0                 not null comment '点赞数',
    favourNum   int      default 0                 not null comment '收藏数',
    userId      bigint                             not null comment '创建用户 id',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '题目' collate = utf8mb4_unicode_ci;
```



## 题目提交表

哪个用户提交了哪道题目，存放判题结果等



提交用户 id：userId

题目 id：questionId

语言：language

用户代码：code

判题状态：status（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）

判题信息（判题过程中得到的一些信息，比如程序的失败原因、程序执行消耗的时间、空间）：

judgeInfo（json 对象）

```json
{
	"message": "程序执行信息",
	"time": 1000, // 单位为 ms
	"memory": 1000, // 单位为KB
}
```

示例代码：

```mysql
-- 题目提交表
create table if not exists question_submit
(
    id         bigint auto_increment comment 'id' primary key,
    language   varchar(128)                       not null comment '编程语言',
    code       text                               not null comment '用户代码',
    judgeInfo  text                               null comment '判题信息（json 对象）',
    status     int      default 0                 not null comment '判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）',
    questionId bigint                             not null comment '题目 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_questionId (questionId),
    index idx_userId (userId)
) comment '题目提交';
```

判题信息枚举值：

- Accepted: 成功
- Wrong Answer: 答案错误
- Presentation Error: 输出格式错误
- Time Limit Exceeded：超时
- Memory Limit Exceeded：内存溢出
- Runtime Error：运行时错误（用户程序的问题）
- Output Limit Exceeded：输出超出限制
- Compile Error：编译错误
- Waiting：等待中
- Dangerous Operation：危险操作
- System Error：系统错误（做系统人的问题）



### 小知识：数据库索引

什么情况下适合加索引？如何选择给哪个字段加索引？

答：首先从业务出发，无论是单个索引、还是联合索引、都要从你实际的查询语句、字段枚举值的区分度、字段类型考虑（where 条件指定的字段)



比如：where userId = 1 and questionId = 2

可以选择根据 userId 和 questionId 分别建立索引（需要分别根据这两个字段单独查询）；也可以选择给这两个字段建立联合索引（所查询的字段是绑定在一起的）

原则上：能不用索引就不用索引，能用单个索引就不用联合 / 多个索引。不要给没区分度的字段加索引（比如性别，就男 / 女）。因为索引也是要占用空间的。





## 后端接口开发

### 后端开发流程

1）根据功能设计库表

2）自动生成对数据库基本的增删改查（mapper 和 service 层的基本功能）

3）编写 Controller 层，实现基本的增删改查的权限校验（复制粘贴（心安理得的话））

4）去根据业务定制开发新的功能 / 编写的代码



更好的方法，编写自己的代码生尘器（https://github.com/liyupi/sql-father-backend-public）



### 代码生成方法

1）安装 MyBatisX 插件

2）根据项目去调整生成配置，建议生成代码到独立的包，不要影响老的包

![1736864917178](images/image01.png)

3）把代码从生成包中移到实际项目对应目录中

4）找相似的代码去复制 Controller

- 单表去复制单表 Controller（比如 question => post）
- 关联表去复制关联表 （比如 question_submit => post_thumb）

5）复制实体类相关的 DTO、VO、枚举值字段（用于接受前端请求、或者业务间传递信息）

updateRequest 和  editRequest 的区别：前者是给管理员更新用的，可以指定更多字段；后者是给普通用户试用的，只能指定部分字段



6)）为了更方便地处理 json 字段中的某个字段，需要给对应的 json 字段编写独立的类，比如：judgeCase，judgeInfo、judgeConfig

示例代码;

```java
/**
 * 题目用例
 */
@Data
public class JudgeCase {

    /**
     * 输入用例
     */
    private String input;

    /**
     * 输出用例
     */
    private String output;

}
```



什么情况下要加业务前缀？什么情况下不加？

加业务前缀的好处，放置多个表都有类似的类，产生冲突；不加的前提，因为i这个类可能是多个业务之间共享的，能够复用的



定义 VO 类：作用是专门给前端返回对象，可以节约网络传输大小，或者过滤字段（脱敏），保证安全性

比如 judgeCase，answer 字段，一定要删，不能直接给用户答案



7）校验 Controller 层的代码，看看除了要调用的方法缺失外，还有无报错

8）实现 Service 层的代码，从对应的已经编写好的实现类复制粘贴，全局替换（比如 question => post）

9）编写 QuestionVO 的 json / 对象转换工具类

10）用同样的方法，编写 questionSubmit 提交类，这次参考 postThumb 相关文件

11）编写枚举类

参考代码：

```java
/**
 * 题目提交编程语言枚举
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public enum QuestionSubmitLanguageEnum {

    JAVA("java", "java"),
    CPLUSPLUS("c++", "c++"),
    GOLANG("golang", "golang");


    private final String text;

    private final String value;

    QuestionSubmitLanguageEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static QuestionSubmitLanguageEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (QuestionSubmitLanguageEnum anEnum : QuestionSubmitLanguageEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
```



编写好基本代码后，记得通过 Swagger 或者编写单元测试去验证



#### 小知识

为了防止用户按照 id 顺序爬取题目，建议把 id 的生成规则改为ASSIGN_ID 而不是从 1 开始自增

示例代码：

```java
/**
 * id
 */
@TableId(type = IdType.ASSIGN_ID)
private Long id;
```



### 查询提交信息接口

功能：能够根据用户 id，或者题目 id，编程语言，题目提交记录，去查询提交记录

注意事项：

仅本人和管理员能看见自己（提交 userId 和登录用户 id不同）提交的代码



实现方案：先查询，再根据权限去脱敏

核心代码：

```java
@Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        // 脱敏: 仅本人和管理员能看见自己（提交 userId 和登录用户 id不同）提交的代码
        long userId = loginUser.getId();
        // 处理脱敏
        if (userId != questionSubmit.getUserId() && !userService.isAdmin(loginUser)) {
            questionSubmitVO.setCode(null);
        }

        return questionSubmitVO;
    }
```



注意，其中的 ID 是根据表达式自动生成的：camelCase(fileNameWithoutExtension())

![1738064568911](images/image02.png)







## 题目浏览页

1）先定义动态参数路由，开启 props 为 true，可以在页面中直接获取到动态参数（题目 id）

```typescript
{
  path: "/view/questions/:id",
  name: "在线做题",
  component: ViewQuestionsView,
  props: true,
  meta: {
    access: ACCESS_ENUM.USER,
  },
},
```

2）定义布局：左侧是题目信息，右侧是代码编辑

3）左侧题目信息：

- tabs 切换展示的内容
- 定义 MdViewer 组件展示题目内容
- 使用 descriptions 组件展示判题配置 https://arco.design/vue/component/descriptions

4）使用 select 组件让用户选择编程语言

在代码编辑器中监听属性的变化，注意监听 props 要使用箭头函数



`todo: 代码编辑器没有更改语言`






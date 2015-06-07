#ButterKnifeProcedure
ButterKnife的原理简述

#Pluggable Annotation Processing

注解处理器
Java5 中叫APT(Annotation Processing Tool)，在Java6开始，规范化为 Pluggable Annotation Processing。

##第一步（收集信息）
找到所有被注解的属性或者方法，将所有的信息收集到对应的“类数据集”中。

##第二步（生成源文件）
根据每一个“类数据集”，生成对应的java源文件。由于这些文件并不是在运行时生成的，因此也无需动态编译，注解处理器运行完成之后，
编译器会处理所有的编译流程。

##第三步（动态注入）
运行时动态注入，即用户常规调用的 ButterKnife.bind(activity)

这一步为了避免蹩脚的调用，使用了运行时反射，但是作者对每一个类进行了缓存，因此，不会对执行效率产生多大影响。

###注
在最新的 ButterKnife 源码（2015.06.08）中，ButterKnife已经重构了部分方法：

ButterKnife#inject -> ButterKnife#bind

@InjectView -> @FindView

等等，具体变化可以去看官方文档，**本文档后续代码使用最新版本代码演示**。

#极简实现演示

##演示代码说明

1. 示例代码由 ButterKnife 简化而来，部分定义和实现有删改，只能绑定 Activity 中的 View 字段
2. 为了避免引入Android平台，但是又需要更直观，所以mock了android的两个类，[Activity](./android/app/Activity.java) 和 [View](./android/view/View.java)
3. 为了避免使用 Pluggable Annotation Processing 过程中的jar包要求，以及波及不必要的java文件，请使用命令行运行演示，直接运行 ./run.sh 即可查看结果
4. 保证 CLASSPATH 中含有tools.jar

##第一步（收集信息）

1. 在每一个类中找到所有被 FindView 注解的字段
2. 每一个需要绑定的字段信息都保存为一个 [FieldViewBinding](./butterknife/internal/FieldViewBinding.java) 对象，比如：

        @FindView(100)
        View vView1;
        得到：
        new FieldViewBinding(vView1, android.view.View, 100)

3. 将字段分类，获取每一个类的“类数据集”[BindingClass](./butterknife/internal/BindingClass.java)，比如， MainActivity 对应的 “类数据集” 如下：

        MainActivity：
            List<FieldViewBinding> fieldViewBindings = new ArrayList<FieldViewBinding>();
            fieldViewBindings.add(new FieldViewBinding(vView1, android.view.View, 100))
            fieldViewBindings.add(new FieldViewBinding(vView2, android.view.View, 200))

##第二步（生成 Bind 工具类源文件）

为了便于在反射时容易实例化生成的类，每一个生成的类都实现了一个 [ActivityBinder<T extends Activity>](./butterknife/internal/ButterKnifeProcessor.java) 接口，因此，根据 [MainActivity](./sample/MainActivity) “类数据集”生成的文件如下：

    package sample;

    import android.view.View;
    import android.app.Activity;
    import butterknife.ButterKnife.ActivityBinder;

    public class MainActivity$$ViewBinder implements ActivityBinder<sample.MainActivity> {
        @Override
        public void bind(sample.MainActivity target) {
            View view;
            view = target.findViewById(100);
            target.vView1 = view; //这里要求 vView1 的访问权限为 package 级别
            view = target.findViewById(200);
            target.vView2 = view;
        }
    }

##第三步（动态注入）
我们在 MainActivity 中调用 ButterKnife#bind，第一件事就是找到对应生成的 Bind 工具类，这里遵循命名规则(在对应类后增加 $$ViewBinder 后缀)，直接使用动态加载并实例化：

    Class<?> activityBindingClass = Class.forName(targetClass.getName() + ButterKnifeProcessor.SUFFIX);
    activityBinder = (ActivityBinder) activityBindingClass.newInstance();

获得相应的 ActivityBinder 之后，使用 ActivityBinder#bind 进行绑定，与手动调用 findViewById 效果相同


##运行

运行：

    ButterKnifeProcedure/src$ ./run.sh

结果：

    mainActivity.vView1.id = 100
    mainActivity.vView2.id = 200










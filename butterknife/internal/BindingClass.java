package butterknife.internal;

import java.util.ArrayList;
import java.util.List;


final class BindingClass {
    private final List<FieldViewBinding> fieldViewBindings = new ArrayList<FieldViewBinding>();
    private final String classPackage; //包名
    private final String className;//类名
    private final String targetClass;//等待绑定的类

    BindingClass(String classPackage, String className, String targetClass) {
        this.classPackage = classPackage;
        this.className = className;
        this.targetClass = targetClass;
    }

    void addField(FieldViewBinding binding) {
        fieldViewBindings.add(binding);
    }

    //Fully Qualified Class Name
    String getFqcn() {
        return classPackage + "." + className;
    }

    //java源文件内容
    String brewJava() {
        StringBuilder builder = new StringBuilder();
        builder.append("package ").append(classPackage).append(";\n\n");
        builder.append("import android.view.View;\n");
        builder.append("import android.app.Activity;\n");
        builder.append("import butterknife.ButterKnife.ActivityBinder;\n");
        builder.append('\n');
        builder.append("public class ").append(className).append(" implements ActivityBinder<").append(targetClass).append("> {\n");
        builder.append("@Override ").append("\n");
        builder.append(String.format("public void bind(%s target) {\n", targetClass));
        builder.append("    View view;\n");

        for (FieldViewBinding fieldViewBinding : fieldViewBindings) {
            builder.append(String.format("    view = target.findViewById(%d);\n", fieldViewBinding.getId()));
            builder.append(String.format("    target.%s = view;\n", fieldViewBinding.getName()));
        }

        builder.append("  }\n");

        builder.append("}\n");
        return builder.toString();
    }
}

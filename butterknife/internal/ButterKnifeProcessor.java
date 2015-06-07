package butterknife.internal;

import butterknife.FindView;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class ButterKnifeProcessor extends AbstractProcessor {
    public static final String SUFFIX = "$$ViewBinder";

    private Elements elementUtils;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);

        elementUtils = env.getElementUtils();
        filer = env.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<String>();
        types.add(FindView.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {

        Map<TypeElement, BindingClass> targetClassMap = new LinkedHashMap<TypeElement, BindingClass>();

        //在每一个类中找到所有被 FindView 注解的字段
        for (Element element : env.getElementsAnnotatedWith(FindView.class)) {
            try {
                parseFindView(element, targetClassMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //为每一个类生成 Bind 工具类
        for (Map.Entry<TypeElement, BindingClass> entry : targetClassMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            BindingClass bindingClass = entry.getValue();
            try {
                JavaFileObject jfo = filer.createSourceFile(bindingClass.getFqcn(), typeElement);
                Writer writer = jfo.openWriter();
                writer.write(bindingClass.brewJava());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    private void parseFindView(Element element, Map<TypeElement, BindingClass> targetClassMap) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        BindingClass bindingClass = targetClassMap.get(enclosingElement);
        if (bindingClass == null) {
            String targetType = enclosingElement.getQualifiedName().toString();
            String classPackage = getPackageName(enclosingElement);
            String className = getClassName(enclosingElement, classPackage) + SUFFIX;
            bindingClass = new BindingClass(classPackage, className, targetType);
            targetClassMap.put(enclosingElement, bindingClass);
        }

        //获取viewId
        int id = element.getAnnotation(FindView.class).value();
        //字段名称
        String name = element.getSimpleName().toString();
        //字段类型
        String type = element.asType().toString();

        FieldViewBinding binding = new FieldViewBinding(name, type, id);

        //将绑定信息添加到对应的类中
        bindingClass.addField(binding);
    }

    //动态生成的 ViewBinder 包名
    private String getPackageName(TypeElement type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    //动态生成的 ViewBinder 所在类名
    private static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }
}

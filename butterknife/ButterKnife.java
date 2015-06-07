package butterknife;

import android.app.Activity;
import butterknife.internal.ButterKnifeProcessor;

public final class ButterKnife {
    public interface ActivityBinder<T extends Activity> {
        void bind(T activity);
    }

    public static void bind(Activity target) {
        Class<?> targetClass = target.getClass();
        ActivityBinder activityBinder;
        try {
            Class<?> activityBindingClass = Class.forName(targetClass.getName() + ButterKnifeProcessor.SUFFIX);
            activityBinder = (ActivityBinder) activityBindingClass.newInstance();
            activityBinder.bind(target);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

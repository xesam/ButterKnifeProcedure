package sample;

import android.app.Activity;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.FindView;


public class MainActivity extends Activity {

    @FindView(100)
    View vView1;
    @FindView(200)
    View vView2;


    public static void main(String[] args) {
        MainActivity mainActivity = new MainActivity();
        ButterKnife.bind(mainActivity);
        System.out.println("mainActivity.vView1.id = " + mainActivity.vView1.id);
        System.out.println("mainActivity.vView2.id = " + mainActivity.vView2.id);
    }
}

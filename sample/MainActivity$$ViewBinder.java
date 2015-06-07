package sample;

import android.view.View;
import android.app.Activity;
import butterknife.ButterKnife.ActivityBinder;

public class MainActivity$$ViewBinder implements ActivityBinder<sample.MainActivity> {
@Override 
public void bind(sample.MainActivity target) {
    View view;
    view = target.findViewById(100);
    target.vView1 = view;
    view = target.findViewById(200);
    target.vView2 = view;
  }
}

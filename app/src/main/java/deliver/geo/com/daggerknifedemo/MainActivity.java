package deliver.geo.com.daggerknifedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "RXjava";
    @BindView(R.id.main_tv)
    TextView mMainTv;

    @BindString(R.string.butter)
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (mMainTv != null)
            mMainTv.setText(title);
//        rXJavaObserver();
        rxJavaTranslate();
    }

    private void rXJavaObserver() {
        //观察者，发送事件
        Observable<String> myObservable = Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(final Subscriber<? super String> sub) {
                        sub.onNext("Hello, world!");
                        sub.onCompleted();
                    }
                }
        );
        //订阅者，处理事件
        Subscriber<String> mySubscriber = new Subscriber<String>() {
            @Override
            public void onNext(String s) {
                showToast(s);
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }
        };
        myObservable.subscribe(mySubscriber);

        //简单版订阅，发送一个字符串
        Observable.just("HelloThread")
                .map(new Func1<String, String>() {   //这个map操作符就是将一个事件转换成另一个事件，比如，这里就是将HelloThread转换成了HelloThread-Dan，最后在传递到Action1的call方法里
                    @Override
                    public String call(String s) {
                        return s + " -Dan";
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        log(s);
                    }
                });
        //这个会按序从6开始发射10次数字出去，即最终发射的数字是6、7、8、9...15
        Observable.range(6, 10).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                log(integer + "");
            }
        });
        //以下通过just会将arr这个数组一次性发出去
        String[] arr = {"a", "b", "c", "d"};
        Observable.just(arr).subscribe(new Action1<String[]>() {
            @Override
            public void call(String[] strings) {
                log(strings.toString() + Arrays.toString(strings));
            }
        });
        //以下通过from操作符将数组一个一个的发送出去，跟just是不一样的
        Observable.from(arr).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                log(s);
            }
        });
        //每隔1秒就会发送一个Long数据，从0开始依次+1然后发送出去。
        Observable.interval(1, TimeUnit.SECONDS).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                log("时间-->" + aLong);
            }
        });
        //repeat（）操作符会重复5此发射“哈哈”这个对象
        Observable.just("哈哈").repeat(5).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                log("重复啊" + s);
            }
        });
    }

    /**
     * Observable转换
     */
    private void rxJavaTranslate() {
        //通过buffer（）操作符可以缓存几个元素然后一起发送，
        //如下是最大缓存2个元素，也就是当缓存够了2个元素就隔三个元素发送，那么下面的发送结果就是：1 2一组，4 5一组，  7 8一组
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9).buffer(2, 3).subscribe(new Action1<List<Integer>>() {
            @Override
            public void call(List<Integer> integers) {
                StringBuilder str = new StringBuilder();
                for (int in : integers) {
                    str.append("按照元素缓存：buffer（）  " + in);
                }
                log(str.toString());
            }
        });
        //如下是按照时间缓存，每次缓存3秒，TimeUnit.SECOND指定单位
        Observable.interval(1, TimeUnit.SECONDS).buffer(3, TimeUnit.SECONDS).subscribe(new Action1<List<Long>>() {
            @Override
            public void call(List<Long> longs) {
                StringBuilder str = new StringBuilder();
                for (long in : longs) {
                    str.append("  " + in);
                }
                log("按照时间缓存：buffer（）" + str.toString());
            }
        });
        // FlatMap操作符是一个用处多多的操作符，可以将要数据根据你想要的规则进行转化后再发射出去
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .flatMap(new Func1<Integer, Observable<?>>() {
                    @Override
                    public Observable<String> call(Integer integer) {
                        return Observable.just("flat map:" + integer);
                    }
                }).subscribe(new Action1<Object>() {
            @Override
            public void call(Object integer) {
                log("转换Observable  flatMap（）" + integer.toString());
            }
        });
        //map操作符直接修改值,这个和flatMap有所不同，flatMap是转换成一个新的Observable，而map是直接转换内容
        Observable.just("你好啊")
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return s + "张三";
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        log("转换内容map()" + s);
                    }
                });
        String[] as = {"a", "b", "c", "c", "c", "d"};
        //去掉重复的,以下C只会发出一次
        Observable.from(as).distinct().subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                log("去重distinct()" + s);
            }
        });
        //只发出第一个元素,如下便只会发射一个a元素出去
        Observable.from(as).first().subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                log("只发出第一个元素first()--->" + s);
            }
        });
        //skip是过滤掉前n项,如下是过滤掉前2项，那么就会连续输出ccccd
        Observable.from(as).skip(2).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                log("跳过前n个元素skip()" + s);
            }
        });
        //take是只取前两个元素
        Observable.from(as).take(2).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                log("只发射前n个元素take()" + s);
            }
        });
        //skipLast是过滤掉后n项,如下是过滤掉后2项
        Observable.from(as).skipLast(2).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                log("跳过后n个元素skipLast()" + s);
            }
        });
        //take是只取后n个元素，如下是取后2个元素
        Observable.from(as).takeLast(2).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                log("只发射后n个元素takeLast()" + s);
            }
        });
        //elementAt()就是取指定位置的元素
        Observable.from(as).elementAt(2).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                log("取第三个元素elementAt（）" + s);
            }
        });
        //filter只会返回满足过来条件的数据，以下就只会返回字符串d
        Observable.from(as).filter(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                return s.equals("d");
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                log("满足过滤条件：filter" + s);
            }
        });
    }

    /**
     * RXJava组合
     */
    private void rxJavaThread() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> sub) {
                sub.onNext("Hello, world!");
                sub.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private void log(String con) {
        Log.e(TAG, con);
    }

    private void showToast(String s) {
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
    }

}

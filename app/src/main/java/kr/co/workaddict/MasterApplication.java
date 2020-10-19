package kr.co.workaddict;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import kr.co.workaddict.Interface.RetrofitService;
import kr.co.workaddict.Login.KakaoSDKAdapter;
import kr.co.workaddict.Utility.Util;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.kakao.auth.KakaoSDK;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MasterApplication extends Application {

    public static MasterApplication application;
    public static RetrofitService service;
    private static final String TAG = "MasterApplication";

    public static MasterApplication getMasterApplication() {
        if (application == null) {

            throw new IllegalStateException("This Application does not inherit com.kakao.GlobalApplication");

        }

        return application;
    }




    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        //스테토를 초기화해줘야 StethoIntercepto가 작동한다
        Stetho.initializeWithDefaults(this);
        createRetrofit();


        // Kakao Sdk 초기화
        KakaoSDK.init(new KakaoSDKAdapter());

    }


    public void createRetrofit() {
        Interceptor header = chain -> {
            //원래 나가던 오리지날을 잡아서 newBuilder(개조) 하는 것이다.
            //원래 나가던 통신에서 헤더를 달아서 다시 보내는 것이다.
            //proceed로 다시 개조한 것을 보낸다.
            Response response = null;
            Request original = chain.request();
            if (checkIsLogin()) {
                if (getUserToken() != null) {
                    //로그인 했을 때 original에 header달아서 response
                    //토큰이 null이 아닐 때 header에 token 달아준다
                    Request request = original.newBuilder()
                            .header("Authorization", Util.APIKEY).build();
                    response = chain.proceed(request);
                }

            } else {
                //로그인 안했을 때 original 그대로 response
                response = chain.proceed(original);
            }
            return response;
        };

        OkHttpClient client = new OkHttpClient.Builder()
                //위에서 만든 header라는 인터셉터를 붙인다. 
                .addInterceptor(header)
                //이번에는 통신을 StethoInterceptor로 낚아챈다
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        //이제 retrofit을 만들어준다.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Util.addressURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        service = retrofit.create(RetrofitService.class);

    }

    //로그인을 했는지 안했는지를 토큰값이 있는지 없는지로 구분할 것이다.
    // 그리고 다시 header interceptor에서 이것을 검사해준다.
    public boolean checkIsLogin() {
        SharedPreferences sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE);
        String token = sp.getString("login_sp", "null");
        if (token != "null") return true;
        else return true;
    }

    public String getUserToken() {
        SharedPreferences sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE);
        String token = sp.getString("login_sp", "null");
        if (token.equals("null")) return "aaa";
        else return token;
    }


    @Override

    public void onTerminate() {

        super.onTerminate();

        application = null;

    }


}

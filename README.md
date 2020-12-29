# Overview
Thư viện Upload sử dụng api upload istorage upload load một hình ảnh từ máy. thông qua đường dẫn đến hình ảnh.thư viện có 2 chức năng chính là upload và getlink, Chức năng upload hình ảnh sau khi upload xong hình ảnh thì thư viện sẽ trả về một filekey tương ướng vơi hình ảnh đã upload lên. chức năng getlink là sử dụng filekey đã upload để lấy về đường dẫn hình ảnh đã upload lên server. 
# How to include in your project
Thêm mã bên dưới vào thư mục gốc build.gradle

    allprojects {
      repositories {
          google()
          jcenter()
          maven { url 'https://jitpack.io' }
      }
    }

Thêm mã bên dưới vào mô-đun build.gradle dependency

    implementation 'com.github.hautran96:upload:1.0'
    
# Usage

    new istorage(context, callback upload, callback getlink)
    
Truyền đường dẫn từ hình ảnh từ máy
    
    new istorage("context").setLinkFile(path)
    
Truyền token api

    new istorage(context, callback upload, callback getlink).setApiKey("api key")
    
Upload hình ảnh 

    new istorage(context, callback upload, callback getlink).upload();
 
Nếu bạn sử dụng chức năng upload thì bận cần phải implements onGetLinkResults: 

    public class MainActivity extends AppCompatActivity implements onGetLinkResults
     @Override
    public void onSuccess(String link) {
        // return key image upload
        Log.i(Constant.TAG, "mFileKey  " + link);
    }
    // sau khi upload xong thì filekey sẽ trả về trong hàm onSuccess
    
Getlink 

    new istorage(context, callback upload, callback getlink).getLink("file key");
               
               
 Nếu bạn sử dụng chức năng getlink thì bận cần phải implements HttpUtils.GetDataCompleted
 
      public class MainActivity extends AppCompatActivity implements HttpUtils.GetDataCompleted
     @Override
      public void onCompleted(String link) {
          // return link image
          Log.i(Constant.TAG, "link " + link);
      } // sau khi getlink xong thì link hình ảnh sẽ trả về hàm onCompleted
 
# Example

  upload: 
  
     new istorage(MainActivity.this, this, null)
                    .setLinkFile(path)
                    .setApiKey(mApiKey)
                    .upload();
               
               
  getlink: 
  
    new istorage(MainActivity.this, null, MainActivity.this)
                        .getLink(mFileKey);
 
    
    

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

Thêm mã bên dưới vào mô-đun build.gradle dependency và chọn sync now (dùng để tải thư viện từ kho github)

    implementation 'com.github.hautran96:upload:1.0'
    
# Usage

     Istorage istorage =  new Istorage.IstorageBuilder("context","callback function")
                .setApiKey(mApiKey)
                .build();
    
Truyền token api

    istorage.setApiKey(mApiKey)
    
Upload hình ảnh 

    istorage.upload("path image"); // truyền vào đường dẫn tới file hình ảnh  
    
Getlink 

    istorage.getLink(mFileKey); // truyền vào file key để lấy link ảnh
               
               
bạn cần phải implements onGetResults
 
    public class MainActivity extends AppCompatActivity implements onGetResults;
    
     @Override
    public void onUpload(String key) {
        Log.i(Constant.TAG, "link " + key);
    }

     @Override
    public void onGetLink(int code, String link) {
        Log.i(Constant.TAG, "code " + code + " link " + link);
    }
 
# Example

  build: 
  
    Istorage istorage =  new Istorage
                .IstorageBuilder("context","callback function")
                .setApiKey(mApiKey)
                .build();

  upload: 

     istorage.upload(path);
                          
  getlink: 
 
    istorage.getlink(mFileKey);

![Library](https://img.shields.io/badge/Dependency-In%20Progress-orange)
### eKyc Library - Netset Software
##### Written by: DEEPAK KUMAR

You have add below lines in your ```build.gradle``` 
```
apply plugin: 'kotlin-kapt'
```
You have to enable dataBinding
```
android {
     ...	
     dataBinding {
  	enabled = true
     }
}

```

#### Installation

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.Mprogrammer2020:eKyc:v1.6'
	}

#### Usage

 ```
val intent = Intent(this,EKycActivity::class.java)
intent.putExtra(Constants.API_KEY,"ADD_YOUR_BASIS_ID_API_KEY_HERE")
startActivityForResult(intent,Constants.BASIS_REQ_CODE)
 ```

#### Handle result
```
 override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.BASIS_REQ_CODE) {

            if (resultCode == Activity.RESULT_OK) {
                Log.e("RESULT","OK")
		
		// You'll get user hash and id from basis.
		Log.e("BASIS USER HASH", data!!.getStringExtra(Constants.BASIS_USER_HASH)!!)
                Log.e("BASIS USER ID", data.getIntExtra(Constants.BASIS_USER_ID,0).toString())

            }

            if (resultCode == RESULT_CANCELED){
                Log.e("RESULT","CANCELED")
            }

        }

    }
```

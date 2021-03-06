# Android Report - Rémi GUILLON BONY

### Explain how you ensure user is the right one starting the app

To ensure that only valid users are accessing the app, I decided to set up a masterkey needed to access the different accounts. I made the choice of a rather unusual masterkey (**ER6n650f**) for reasons I will describe later.
To avoid having the masterkey hard-coded in my code in order to compare it with the key given by the user, I decided to instead compare the hash of both keys.
I first manually obtained the hash of ER6n650f and wrote it in my "if" condition. Then, each time a key is given by the user, its hash is obtained thanks to the SHA256 algorithm and compared with the stored hash. If both hash are similar, the user is validated.


To ensure the security of the hash, I made 3 choices :
- First I checked online if my masterkey was easily retrievable given its hash thanks to rainbow tables. This is what lead me to choose the ER6n650f masterkey instead of the 'masterkey' password that I previously had. For example my masterkey cannot be retrieved using the hash on this website : https://crackstation.net/

- Secondly, I decided to store the hash in a C++ file instead of a regular kotlin file. Indeed the laters tend to be easily decompiled even by free online decompilators, whereas C++ files are more resilient. I also created the CMakeList.txt file and the corresponding functions and build.gradle modifications in order to allow the use of C++ ressources with Kotlin.

- Finally the hash itself is encrypted using PolyEncryption. I have manually encrypted it using the masterkey as key and written the encrypted version of it in the code. 
To access the app, the code will try to decrypt the hash using the entered key, and then compare the result of this decryption with the hash of the entered key. 
Without this element of security, if one could manage to decompile my C++ code he would find the hash of my masterkey and maybe eventually find a rainbow table containing this hash. With this added layer of security, this would be impossible.

Just like the masterkey's hash, all the information contained in the C++ file has previously been encrypted using PolyEncryption and the masterkey as key for this operation. Indeed I could not just save a plain text key/API/... in a C++ file because it would only grant me the security of the obfuscation.
Therefore I decided that the best course of action was to use the key given by the user for encryption and decryption. Indeed, this masterkey is already protected by the hash and its complexity regarding rainbow tables.
If a user wants to use my application, the code will reach the point of data encryption and decryption only if he is validated so the only encryption key ever used will always be my masterkey.
If a malevolent user manages to decompile the C++ files and find my masterkey's hash, the names of the data files or another critical information, then he still can't get access to it because they are encrypted and protected by the inherent complexity of the masterkey's hash (and its encryption).

### How do you securely save user's data on your phone ?

In order to allow offline use of my application, I decided to save the online data on the internal storage of the phone each time a successful connection to the API was made. This data is saved in 2 files which names are also saved on the C++ ressources, making them harder to retrieve. 
The names are also encrypted using PolyEncryption. Just like the masterkey's hash, a previously encrypted version (using the masterkey as key) of these names is written in the C++ file. Once the app needs to write or retrieve data in those files, a PolyDecryption using the user's entered key is used on these filenames to get a decrypted version of them. In the event of an attacker managing to decompile the C++ file, this would prevent him from easily finding the files names.

In order to further protect the access to the data, I decided to also use a PolyEncryption based on the masterkey on the data saved in these files. This way, even retrieving the files would not grant access to the data contained in them.

### How did you hide the API url ?

First, I decided to place the API URL in my C++ to ensure some degree of obfuscation.
In order to increase the obfuscation, I also turned on the option to auto-obfuscate integrated in Android Studio (minifyEnabled true with a defined proguard file).

To further protect my API, I also encrypted it with PolyEncryption and with the masterkey as key for the same reasons as mentionned above. This way even if my API is retrieved in the code, it is unusable.

### Add screenshots of your application

The screenshots are available in the "screenshots" folder. Here is a breakdown of each screenshot :

1 - Login page, entering wrong credentials will result in the Toast showing up. If the masterkey is correct but the id is an integer greater than what is available on the API, the app will try to find it in the localy saved files before showing up the same Error Toast.

2 - Accounts page using online data. A Toast shows up to signal which data is used, local or online.

3 - Accounts page using local data. The Toast warns the user that local data is being used instead of online data.


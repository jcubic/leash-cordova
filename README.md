## Leash Shell Cordova Plugin

In order to use this you need to install cordova from npm, java jdk 8 and android sdk

on Ubuntu you can install `umake` and run:

```
umake android
```

it will install java jdk, android studio and android sdk then you need to set
ANDROID_HOME to point to android sdk and add sdk/tools, android-studio/graddle/gradle-version/bin and sdk/platform-tools to your PATH.

then and run:

```
npm run install
cordova run android
```

it should build the project and deploy it to your phone if you're connected via usb cable and debug via usb is turn on.

If you modify the service plugin that's in `./backend` directory you can run:

```
npm run plugin
```

to remove update the plugin from `./backend` directory


### Limitations

The full shell is available only when you have rooted phone or when you connect to computer and enable USB debug mode.

### License

Licensed under [MIT](http://opensource.org/licenses/MIT) license

Copyright (c) 2017 [Jakub Jankiewicz](http://jcubic.pl/jakub-jankiewicz)


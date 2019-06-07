# Tag You're It

<a href="https://play.google.com/store/apps/details?id=tagyourit.spelder.tagyourit"><img src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" height="48"></a>
for Android ([beta](https://play.google.com/apps/testing/tagyourit.spelder.tagyourit)) 

Tag You're It is an android app for learning barbershop tags. It allows you to easily find your next favorite tag from barbershoptags.com anywhere. Then you can view the sheet music and play the learning tracks without leaving the app. 

## Getting Started

### Prerequisites

 * JDK 1.8
 * Android SDK
   * Android NDK

### Installing

1. Clone repository
2. Import project into Android Studio
3. Enable the Youtube Data API and create API key - https://developers.google.com/youtube/v3/getting-started
4. Create "apiKeys.properties" file in the base directory and give contents
`YOUTUBE_API_KEY="<api_key>"`
5. Enable Google Drive API - https://developers.google.com/drive/api/v3/enable-drive-api
6. Run app in Android Studio

## Running the tests

In Android Studio, right click on project -> app -> src -> androidTest -> java -> com.spelder.tagyourit and click "Run Tests in ...". Select valid connected or virtual devices.

## Contributing

Please read [CONTRIBUTING.md](https://github.com/spelderdev/tagYou-reIt/blob/master/CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Authors

* **Trevor Holder** - *Initial work*

See also the list of [contributors](https://github.com/spelderdev/tagYou-reIt/graphs/contributors) who participated in this project.

## License

This project is licensed under the GPLv3 License - see the [LICENSE.md](https://github.com/spelderdev/tagYou-reIt/blob/master/LICENSE) file for details

## Acknowledgments

* [BottomSheet](https://github.com/Flipboard/bottomsheet)
* [Android PdfViewer](https://github.com/barteksc/AndroidPdfViewer)
* [TouchImageView](https://github.com/MikeOrtiz/TouchImageView)
* [Pitch Shifting](http://blogs.zynaptiq.com/bernsee/pitch-shifting-using-the-ft/)
* [android-youtube-player](https://github.com/PierfrancescoSoffritti/android-youtube-player)
* [YouTube Android Player API for Gradle](https://github.com/davidmigloz/youtube-android-player-api-gradle)
* [Android Support library - preference v7 bugfix](https://github.com/Gericop/Android-Support-Preference-V7-Fix)

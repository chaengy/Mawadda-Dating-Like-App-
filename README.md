# Mawadda 3.0

Mawadda 3.0 is a university project focused on developing a Muslim dating application. This project is not intended for official publication and is part of an academic exercise.

## Table of Contents
- [Project Overview](#project-overview)
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

## Project Overview
Mawadda 3.0 aims to create a platform where Muslim individuals can find potential life partners. This app includes features such as profile creation, chat functionality, and match suggestions based on user preferences.

## Features
- User authentication and profile creation
- Match suggestions based on preferences
- Chat functionality between matched users
- Filter options for better match results (currently not fully functional)

## Installation
To get a local copy up and running, follow these steps:

### Prerequisites
- Android Studio
- Java Development Kit (JDK)
- Firebase account

### Steps
1. Clone the repository:
    ```sh
    git clone https://github.com/yourusername/mawadda3_0.git
    ```
2. Open the project in Android Studio.
3. Set up Firebase and connect it to the project:
    - Follow the instructions on the [Firebase website](https://firebase.google.com/docs/android/setup) to add Firebase to your Android project.
    - Replace the `google-services.json` file in the `app` directory with your own configuration file from Firebase.
4. Build the project to download necessary dependencies.
5. Run the application on an emulator or connected device.

### Dependencies
- For the latest GitHub and Google dependencies, refer to their respective latest releases:
    - [GitHub Android SDK](https://github.com/github/android)
    - [Google Play Services](https://developers.google.com/android/guides/overview)

## Usage
After setting up the project, you can log in or sign up to create a profile. Once logged in, you can browse through suggested matches, initiate chats, and adjust your match preferences using the filter options (note: the filter feature is not fully functional).

## Project Structure
```
mawadda3_0/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/mawadda3_0/
│   │   │   ├── res/
│   │   ├── androidTest/
│   │   ├── test/
│   ├── build.gradle
│   ├── google-services.json
│   └── proguard-rules.pro
├── build.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
├── settings.gradle
└── .gitignore
```

## Contributing
Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License
Distributed under the MIT License. See `LICENSE` for more information.

---

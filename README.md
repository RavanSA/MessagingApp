<div style="width:100%">
    <div style="width:50%; display:inline-block">
        <p align="center">
        <img align="center" alt="" src="https://github.com/RavanSA/MessagingApp/blob/main/app/src/main/res/drawable/logo_gif.gif">
        </p>
    </div>
</div>

<br></br><br></br>



# Chat Application Using Artificial Emotional Intelligence
> The application is not like an ordinary chat application, but aims to keep its place in the market with a clean user interface, a solid application structure over a user-friendly and secure network.
> The app will provide a modern approach to chat apps using new technologies for the community. 
> It aims to use more machine learning approaches to determine a person's emotional state and then react to it, and try to create artificial emotional intelligence.
> Live demo can be found [_here_](https://play.google.com/store/apps/xxxxxxxxxxxxxx) "not yet available". 


![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg)
!Language(https://img.shields.io/badge/Language-Kotlin-yellowgreen.svg)
![GitHub repo size](https://img.shields.io/github/repo-size/RavanSA/MessagingApp)
![GitHub contributors](https://img.shields.io/github/contributors/RavanSA/MessagingApp)
![GitHub stars](https://img.shields.io/github/stars/RavanSA/MessagingApp)

## Prerequisites :star:

Before you begin, ensure you have met the following requirements:<br/>
✅ &nbsp; You have `Android Studio` installed in your machine <br/>
✅ &nbsp; You have a Android Device or Emulator with Android Version 6.0 or above. <br/>
✅ &nbsp; You have read [SRS](https://github.com/RavanSA/MessagingApp/blob/main/doc.pdf).<br/>


## Installing Android Kotlin Chat App
Simply clone the repository from [here](https://github.com/RavanSA/MessagingApp/archive/refs/heads/main.zip)
---


## Table of Contents
* [General Info](#general-information)
* [Technologies Used](#technologies-used)
* [Features](#features)
* [Screenshots](#screenshots)
* [Project Status](#project-status)
* [Room for Improvement](#room-for-improvement)
* [Acknowledgements](#acknowledgements)
* [Contact](#contact)
* [License](#license)


##  General Information
Our applications enable users to communicate with each other and communicate securely by using new
technologies. We will provide a great app for people to communicate with each other that they know
using their contact lists whether they give permissions. The main functionality of the application gives the
opportunity to the user. These are described below:
- send and receive text messages
- sending images and files
- scheduling messages
- sending location
- sending voice recording
- voice and video calling with end-to-end encryption
Another functionality is that it offers features such as taking photos, adding filters, and editing images.
We also mentioned earlier that we will be implementing machine learning applications such as image
recognition and sentiment analysis. For the profile picture, the application will force to the user set the
profile picture as a real picture of themselves. Some machine learning applications will be applied to our
applications. Some of them described below:
- Emotion Recognition
It is a subset of artificial intelligence that measures, understands, simulates, and reacts to human emotions.
It’s known as artificial emotional intelligence. The AI will try to determine your emotional expression based on their dialogues in the chat application.
In case you’re wondering, this is how some of those are 8 emotions: neutral, happy, sad, surprise, fear, disgust, anger, and contempt.
Dataset : We will take a Twitter dataset which is provided by Kaggle for
text classification competitions. It includes 40,000 tweets labeled respectively
to 13 different human emotions. The result of the model will be presented in the user interface using color psychology according to the result of the model.
Preprocessing : Stemming and lemmatization are two crucial steps of pre-processing.
In stemming, we will convert words to their root form by truncating suffixes. 
This process reduces the unwanted computation of sentences.
Lemmatization involves morphological analysis to remove inflectional endings from a token to turn it into the base word lemma.
Model: We will use Bidirectional LSTM for model.
Bidirectional long-short term memory (Bi-LSTM) is a Recurrent Neural Network architecture where makes use of information in both directions forward or backward.
<img align="center" width="100%" height="auto"
src="<img align="center" width="100%" height="auto"
src="https://github.com/RavanSA/MessagingApp/blob/main/app/src/main/res/drawable/UI.jpg">
- Age Estimation
Dataset: We will train our model on the UTKFace dataset which contains 23K images.
Preprocessing: We use 3-channeled RGB images as input. The age estimation model takes in 200 * 200 images as input.
Model: We will use CNN for face recognition. A CNN is trained to detect and recognize face images.
The model will build using Keras Convolution 2D layers.
- Gender Estimation
Dataset: The dataset for gender estimation will be same as the dataset we will use for age estimation. 
Preprocessing: We use 3-channeled RGB images as input. The age estimation model takes in 128 * 128 images as input.
Model: The model will be the same as the age estimation with a small difference to adapt the binary classification.
- Generate anime faces from human faces

## Technologies Used
- Android Studio - Arctic Fox (2020.3.1)
- Kotlin - 203-1.6.10-RC-release-906-AS7717.8
- Firebase - 20.0.3
- Python - 3.6.9
- Google Colab
- WebRTC - v1.0.32006
- Room - v2.4.2
- Tensorflow - 19c


## Features
- Registration with OTP verification using Firebase Authentication
- One to one chat
- Message status for failed,sent,delivered and seen - InProgress
- Supported message types
	- Text
	- Voice - Inprogress
	- Image - Inprogress
	- File - Inprogress
	- Location - Inprogress
- Typing status
- Online, Last online
- Notification when message received
- Search contacts by username 
- Local storage when no internet connection available
- Image editing when updating profile picture - Inprogress
- Voice and video call using WebRTC - InProgress
- Schedule message - InProgress
- End-to-end encryption - InProgress
- Block User - InProgress
- Favorite Messages - InProgress
- Update Information
- Persistence offline authentication
- Fetching users contacts
- Upload profile picture with using local storage and camera
This list can be extended

## Screenshots
<img align="center" width="100%" height="auto"
src="https://github.com/RavanSA/MessagingApp/blob/main/app/src/main/res/drawable/UI.jpg">


## Project Status
Project is: InProgress

## Room for Improvement
Include areas you believe need improvement / could be improved. Also add TODOs for future development.

Room for improvement:
- Improvement to be done 1
- Improvement to be done 2

To do:
- Feature to be added 1
- Feature to be added 2

## Acknowledgements
Give credit here.
- This project was inspired by...
- This project was based on [this tutorial](https://www.example.com).
- Many thanks to...


## Contact
Created by [@flynerdpl](https://www.flynerd.pl/) - feel free to contact me!



## License
```
MIT License

Copyright (c) [2022] [Ravan SADIGLI]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

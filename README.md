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
<br/>

![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg)
![Language](https://img.shields.io/badge/Language-Kotlin-yellowgreen.svg)
![GitHub repo size](https://img.shields.io/github/repo-size/RavanSA/MessagingApp)
![GitHub contributors](https://img.shields.io/github/contributors/RavanSA/MessagingApp)
![GitHub stars](https://img.shields.io/github/stars/RavanSA/MessagingApp)
<br/>
## Prerequisites 

Before you begin, ensure you have met the following requirements:<br/>
- You have `Android Studio` installed in your machine <br/>
- You have a Android Device or Emulator with Android Version 6.0 or above. <br/>
- You should read [SRS](https://github.com/RavanSA/MessagingApp/blob/main/doc.pdf) documentation.<br/>


## Install Chat App <br/>
Simply clone the repository from [here](https://github.com/RavanSA/MessagingApp/archive/refs/heads/main.zip)


## Table of Contents
* [General Info](#general-information)
* [Technologies Used](#technologies-used)
* [Features](#features)
* [Screenshots](#screenshots)
* [Project Status](#project-status)
* [Future Works](#future-works)
* [Contact](#contact)
* [License](#license)


##  General Information <br/>
Our applications enable users to communicate with each other and communicate securely by using new
technologies. We will provide a great app for people to communicate with each other that they know
using their contact lists whether they give permissions. The main functionality of the application gives the
opportunity to the user. These are described below <br/>
- send and receive text messages
- sending images and files
- scheduling messages
- sending location
- sending voice recording
- voice and video calling with end-to-end encryption <br/>
Another functionality is that it offers features such as taking photos, adding filters, and editing images.
We will also be implementing machine learning applications. For the profile picture, the application will force to the user set the
profile picture as a real picture of themselves. Some machine learning applications will be applied to our
applications. Some of them described below:<br/>
- **Emotion Recognition** <br/>
It is a subset of artificial intelligence that measures, understands, simulates, and reacts to human emotions.
It’s known as artificial emotional intelligence. The AI will try to determine your emotional expression based on their dialogues in the chat application.
In case you’re wondering, this is how some of those are 8 emotions: neutral, happy, sad, surprise, fear, disgust, anger, and contempt. <br/>
*Dataset* : We will take a Twitter dataset which is provided by Kaggle for
text classification competitions. It includes 40,000 tweets labeled respectively
to 13 different human emotions. The result of the model will be presented in the user interface using color psychology according to the result of the model.
<br/>*Preprocessing* : Stemming and lemmatization are two crucial steps of pre-processing.
In stemming, we will convert words to their root form by truncating suffixes. 
This process reduces the unwanted computation of sentences.
Lemmatization involves morphological analysis to remove inflectional endings from a token to turn it into the base word lemma.
<br/>*Model*: We will use Bidirectional LSTM for model.
Bidirectional long-short term memory (Bi-LSTM) is a Recurrent Neural Network architecture where makes use of information in both directions forward or backward. <br/>
<img align="center" width="100%" height="auto"
src="https://miro.medium.com/max/766/1*10K6EwcZKtuyR6Y2t7chNA.jpeg">    <br/>
- **Age Estimation**  <br/>
*Dataset*: We will train our model on the UTKFace dataset which contains 23K images.
<br/>*Preprocessing*: We use 3-channeled RGB images as input. The age estimation model takes in 200 * 200 images as input.
<br/>*Model*: We will use CNN for face recognition. A CNN is trained to detect and recognize face images.
The model will build using Keras Convolution 2D layers. <br/>
- **Gender Estimation** <br/>
<br/>*Dataset*: The dataset for gender estimation will be same as the dataset we will use for age estimation. 
Preprocessing*: We use 3-channeled RGB images as input. The age estimation model takes in 128 * 128 images as input.
<br/>*Model*: The model will be the same as the age estimation with a small difference to adapt the binary classification.

## Technologies Used
- Android Studio - Arctic Fox (2020.3.1)
- Android Jetpack library
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
- Upload profile picture with using local storage and camera <br/>
This list can be flexible

## Screenshots <br/>
<img align="center" width="100%" height="auto"
src="https://github.com/RavanSA/MessagingApp/blob/main/app/src/main/res/drawable/UI.jpg">

## Project Status
The project is currently under development. Expected completion date is June 25, 2022.

## Future Works
- Generate anime faces using users profile pictures<br/>
Using the DCGAN algorithm, we will build a model to generate an anime picture based on users' profile pictures. When the user updates their profile picture, the user will be able to create that picture
and they will have a dialog bar to set the profile picture.
- QR-code sharing
- Web application
- Single page website to get information about the app

## Contact
Created by <a href="mailto:revan.sadiqli99@gmail.com">Ravan SADIGLI</a>.  - feel free to contact me!


## License<br/>
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

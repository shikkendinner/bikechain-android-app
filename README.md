# Bikechain Waitlist Android App
This Android application was built for a non-profit organization called Bikechain, and located near University of Toronto. Bikechain are a hub for cycling activity by providing DIY services, affordable repairs, unique educational opportunities, and a welcoming community of staff and volunteers. Specifically the application was developed for their DIY service, where customers are provided the tools and help of experienced staff and volunteers to fix and maintain their own bikes.

## What Does It Do
The application was built to allow customers of Bikechain to know their position on the waitlist while they waited for their turn to service their own bike. If they were the next to use one of the service stands, they were notified by either opening the application and checking their status, or by a notification on your Android phone while the app was not open (but still running). 

## How Does It Work
The waitlist was stored on a Google Spreadsheet file. Google Apps Script (GAP) files were utilized to access the spreadsheet to see if a name was on the waitlist, to check which position a particular name was at, and to see if a particular name was first on the waitlist (as a result, the next customer on a stand). 

Once the application is opened, a user can enter a name to see where a particular name was on the list. A HTTP post request is sent to one of the GAP files that is running and will handle this request. The response will either display the position of the name queried or an error message (this will repeat every 30 seconds). 

In the background, a token ID is generated, which is unique to the Android smartphone on which the application is being run, and it is sent to a GAP file that will store the token ID in a spreadsheet. This token ID is vital in allowing the Google Cloud Messaging (GCM) API to send a notification to the correct smartphone once a particular user is first on the waitlist. At the same time, a listener is set up on the Android device in order to wait for a notification message from GCM. This triggers a notification to be generated within the Android device that indicates to a user that they are next to use a stand. Allows the user to not have to check the application every single time, and just leave it open (in the background) and wait for a notification to pop up.

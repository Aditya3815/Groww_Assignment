# Groww Assignment 

In this project I followed Clean Architechture and fetch the data from API. you can see stock rates and price charts. You can directly download the app in [Release](https://github.com/Aditya3815/Groww_Assignment/releases/tag/v1.0.0) section.
Here is the demo.

## Demo
https://github.com/user-attachments/assets/eaaf5a0f-8589-4c46-bb27-a260daecc4f5

## Project Modules

```yaml
project-root/ Groww_Assignment
│
├── data/
│   ├── local/
│   │   ├── database/
│   │   │   ├── dao/
│   │   │   └── entities/
│   │   ├── datastore/
│   │   └── cache/
│   ├── remote/
│   │   ├── api/
│   │   ├── dto/
│   │   └── interceptors/
│   ├── repository/
│   └── mappers/
│
├── domain/
│   ├── model/
│   ├── repository/
│   ├── usecase/
│   └── util/
│
├── presentation/
│   ├── navigation/
│   ├── components/
│   │   ├── common/
│   │   ├── stock/
│   │   ├── watchlist/
│   │   ├── search/
│   │   └── chart/
│   ├── screens/
│   │   ├── explore/
│   │   │   └── components/
│   │   ├── stock_detail/
│   │   │   └── components/
│   │   ├── watchlist/
│   │   │   └── components/
│   │   └── view_all/
│   │       └── components/
│   └── util/
│
├── di/
│
└── util/

```

## Setup Instructions
   - First sign up at [alphavantage.co](https://www.alphavantage.co/support/#api-key) and get your free API key.

   - Open `local.properties`
   - Add this line:

     ```
     API_KEY=your_api_key_here
     ```



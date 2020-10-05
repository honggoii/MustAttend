var express = require('express');
var app = express();

var inputData;

var mongoose = require('mongoose');
var multer = require('multer'); //2020-09-08 이미지 업로드를 위한 multer 모듈
var upload =multer({
  storage: multer.diskStorage({
    destination: function (req, file, cb) {
      cb(null, 'uploads/');
    },
    filename: function (req, file, cb) {
      cb(null, file.originalname);
    }
  }),
}); //2020-09-08 uploads 미들웨어

//mongodb 연결
mongoose.connect('mongodb://localhost:27017/mustattend');
var db = mongoose.connection;
db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function callback(){
  console.log("mongo db connection OK.")
});

var reviewSchema = mongoose.Schema({
        store:{type:String,default:null},
      content:{type:String,default:null}
});

var foodSchema = mongoose.Schema({
  name:{type:String, default:null},
  price:{type:String, default:null}
});

var reservationSchema = mongoose.Schema({
        num:{type:Number, default:0},
        state:{type:Number,default:null},
        store:{type:String,default:null},
         purpose:{type:String,default:null},
         dateMonth:{type:Number,default:null},
         dateDay:{type:Number,default:null},
         timeHour:{type:Number,default:null},
         timeMin:{type:Number,default:null},
         numberOfPeople:{type:Number,default:null}
});


var user = mongoose.Schema({
   email:String,
   password: String,
   phoneNum:String,
   year: Number,
   month:Number,
   day:Number,
   store:{
      name:{type:String,default:null},
      permission:{type:Number,default:null},
      address:{type:String,default:null},
      phoneNum:{type:String,default:null},
      license:{type:String,default:null},
      parking:{type:String,default:null},
      maxclientnum:{type:Number,default:null},
      image:{type : String, default:null},   //2020-09-08
      locationX:{type:Number,default:null},
      locationY:{type:Number,default:null},
      menu:[foodSchema],
      menu1:{type:String,default:null},
      menu2:{type:String,default:null},
      menu3:{type:String,default:null},
      price1:{type:String,default:null},
      price2:{type:String,default:null},
      price3:{type:String,default:null},
   },
   review:[reviewSchema],
   locationX:{type:Number,default:null},
   locationY:{type:Number,default:null},
   reservation:[{
     num:{type:Number, default:0},
     state:{type:Number,default:null},
     store:{type:String,default:null},
      purpose:{type:String,default:null},
      dateMonth:{type:Number,default:null},
      dateDay:{type:Number,default:null},
      timeHour:{type:Number,default:null},
      timeMin:{type:Number,default:null},
      numberOfPeople:{type:Number,default:null}
   }
   ]
},
{
   versionKey:false
});

var User = mongoose.model('User',user);
var ReservationSchema = mongoose.model('ReservationSchema',reservationSchema);


/*-----------------------------
사진 업로드 부분 2020-09-08 ( 추후에 가게등록자 회원가입과 병합 )
--------------------------*/
//function 실행 전, single이라는 미들웨어가 실행되어 파일 데이터 req에 포함시켜줌
app.post('/UploadImage',upload.single('image'), function(req, res) {
  var email;

  //res.json(req.file); //이미지 서버(내 노트북)에 저장하기
  console.log(req.file); //파일 내용 콘솔에 찍어보기
  console.log(req.body.email); //이메일 이름 찍어보기
  email = req.body.email; //이메일 값 복사

  //가게 등록자의 기존 가게 정보를 쿼리하기
  User.findOne({email:email},function(err, find_user){
    console.log("findOne()들어옴");
   if(err){
     console.log("find failed");
   }
     console.log(find_user);
     console.log(req.file.path);
     find_user.store.image = req.file.path;        //2020-09-21 db에 이미지 경로 저장
     find_user.save(function(error,data){
     if(error){
          console.log("저장 실패");
      }else{
          console.log('이미지 경로 저장 완료');
          res.send("OK!");
      }
     });

 });




});


/*-----------------------------
예약자 회원 가입 부분
--------------------------*/
app.post('/post', (req, res) => {
   console.log('android node.js connect');

   req.on('data', (data)=>{
     inputData = JSON.parse(data);

   });

   req.on('end', ()=> {
    console.log("email : "+inputData.email+ " , pass : "+inputData.password);

   var newUser = new User({
   email:inputData.email,
   password: inputData.password,
   phoneNum:inputData.phoneNum,
   year: inputData.Year,
   month:inputData.month,
   day:inputData.day,
   });
   newUser.save(function(error,data){
   if(error){
        console.log(error);
    }else{
        console.log('Saved!')
    }
   });


})
   res.write("OK!");
   res.end();
});

/*-----------------------------
  가게등록자 회원가입 부분
--------------------------*/
app.post('/register', (req, res) => {
   //안드로이드에서 데이터 받기
   req.on('data', (data)=>{
     inputData = JSON.parse(data);

   });

   req.on('end', ()=> {
     console.log("예약자 가게등록자 가입 부분");
     console.log("가게 이름 : "+inputData.StoreName+ " , 가게 주소 : "+inputData.StoreAddress);
     var newUser = new User({
       email:inputData.email,
       password: inputData.password,
       phoneNum:inputData.phoneNum,
       year: inputData.Year,
       month:inputData.month,
       day:inputData.day,
       store:{
         /*2020.09.20*/
        locationX:inputData.locationX,
        locationY:inputData.locationY,
        /*2020.09.20*/
          name:inputData.StoreName,
          address:inputData.StoreAddress,
          phoneNum:inputData.StorePhone,
          license:inputData.StorePrivateNum,
          parking:inputData.parking,
          maxclientnum:inputData.Capacity,
          image:inputData.image
       }
   });
   console.log(newUser);
   newUser.save(function(error,data){
   if(error){
        console.log(error);
    }else{
        console.log('Saved!')
    }
   });
});
   res.write("OK!");
   res.end();
});

/*-----------------------------
login 검사 부분
--------------------------*/
app.post('/login', (req, res) => {
  var email;
  var password;
   console.log('android node.js for login connect');
   var result;
   req.on('data', (data)=>{
     inputData = JSON.parse(data);
     email = inputData.email;
     password = inputData.password;
   });

   req.on('end', ()=> {
    console.log(email,password);
    User.findOne({email:email, password:password}, function(err, find_user){//유일한 한 명 찾기
     if(err){//에러
       console.log("find failed");
     }
     if(isEmpty(find_user)){//회원이 아니면
       console.log("들어왔다들어왔다")
       res.write("NO");//NO전송
       res.end();
     }
     else {
       if(!find_user.store.name) {//가게 등록 정보가 없으면
         console.log("이건 예약자")
         res.write("1");
         res.end();
       }
       else {//가게 등록 정보가 있으면
         console.log("이건 가게등록자")
         res.write("2");//구분
         res.end();
       }}
});


});
});

var isEmpty = function(value)
{
  if( value == "" || value == null || value == undefined || ( value != null && typeof value == "object" && !Object.keys(value).length ) )
  {
    return true
   }else{
     return false
   }
};

/*---------------------------

사용자의 개인 정보 출력하는 부분

---------------------------*/
app.post('/checkuser', (req, res) => {
  var email;
   console.log('******************사용자 정보 가져오기*************************');
   var result;
   req.on('data', (data)=>{
     inputData = JSON.parse(data);
     email = inputData.email;

   });

   req.on('end', ()=> {
    console.log(email);
    User.find({email:email}, function(err, find_user){
     if(err){
       console.log("find failed");
     }
       console.log(find_user);
       res.send(find_user); //개인 정보 전송
       res.end();


   });

})


});


/*---------------------------

가게 등록자 인지 검사하고 맞다면 정보 출력 부분

---------------------------*/
app.post('/checkowner', (req, res) => {
  var email;
   console.log('******************android node.js for check owner connect*************************');
   var result;
   req.on('data', (data)=>{
     inputData = JSON.parse(data);
     email = inputData.email;

   });

   req.on('end', ()=> {
    console.log(email);
    User.find({email:email}, function(err, find_user){
     if(err){
       console.log("find failed");
     }
       console.log(find_user);

       if(false){ //가게 등록자가 아니라면
         console.log("no");
         //console.log(find_user.store.name);
         res.write("NO");
         res.end();
       }
       //가게 등록자라면
       else{
         console.log("yes");
        res.send(find_user); //가게 정보 전송
        res.end();
       }



   });

})


});


/*-----------------------------
  위치 저장 부분
--------------------------*/
app.post('/savelocation', (req, res) => {
   //안드로이드에서 데이터 받기
   var email;
   req.on('data', (data)=>{
     inputData = JSON.parse(data);
     email = inputData.email;
   });

   req.on('end', ()=> {
     console.log("예약자 위치 저장 부분");
     console.log("위도 : "+inputData.locationX+ " , 경도: "+inputData.locationY);

     var filter ={email:email};
     var update = {locationX:inputData.locationX,locationY:inputData.locationY};

     /*email로 User 찾아서 위도, 경도 update*/
     User.findOneAndUpdate(filter,update, function(err, data){
     if(err){
       console.log("업데이트 실패");
     }
     else{
       console.log('Saved!')
     }
     });
   });

   res.write("OK!");
   res.end();
});





/*---------------------------

가게 정보 수정 부분

---------------------------*/
app.post('/modifystore', (req, res) => {
  var email;
  var storename;
//var storeaddress;
  var storephone; //전화번호
  var parking; //주차
  var capcity; //수용인원
  var menu1, menu2, menu3, price1,price2,price3; //대표메뉴, 가격
  //var image;
   console.log('*********가게정보수정********************');
   var result;
   req.on('data', (data)=>{
     inputData = JSON.parse(data);

   });

   req.on('end', ()=> {
    console.log(email);

      email=inputData.email;
      //storename=inputData.StoreName; //가게 이름
     //address=inputData.StoreAddress; //가게 주소(문자열) X
       storephone=inputData.StorePhone; //가게 전화번호
       parking=inputData.parking; //주차 가능?
       capcity=inputData.Capacity; // 수용 가능 인원
         //image=inputData.image //사진
         menu1 = inputData.menu1; //대표메뉴, 가격 2020-06-29
         menu2 = inputData.menu2;
         menu3 = inputData.menu3;
         price1 = inputData.price1;
         price2 = inputData.price2;
         price3 = inputData.price3;
         //가게 등록자의 기존 가게 정보를 쿼리하기
       User.findOne({email:email},function(err, find_user){
        if(err){
          console.log("find failed");
        }
          console.log(find_user);
          //find_user.store.name = storename;
          find_user.store.phoneNum = storephone;
          find_user.store.parking = parking;
          find_user.store.maxclientnum = capcity;
          find_user.store.menu1 = menu1;
          find_user.store.menu2 = menu2;
          find_user.store.menu3 = menu3;
          find_user.store.price1 = price1;
          find_user.store.price2 = price2;
          find_user.store.price3 = price3;
          //find_user.store.image = image;        //DB에 image field 만들면 그 때 주석 해제
          find_user.save(function(error,data){
          if(error){
               console.log(error);
           }else{
               console.log('Saved!')
               res.write("OK!");
               res.end();
           }
          });



      });

  /*newUser.find(function(error,data){
  if(error){
       console.log(error);
   }else{
       console.log('Saved!')
   }
 });*/

})


});


/*-----------------------------
특정 가게 정보 조회
--------------------------*/
app.post('/showstoreinfo', (req, res) => {
  var email;
  var password;
   console.log('android node.js for show store info connect');
   var result;
   req.on('data', (data)=>{
     inputData = JSON.parse(data);
     email = inputData.email;
     password = inputData.password;
   });

   req.on('end', ()=> {
    console.log(email,password);
    User.find({email:email,
     password:password}, function(err, find_user){
     if(err){
       console.log("find failed");
     }
     if(find_user != null)
     {
       console.log("*********1**********");
       console.log(find_user);
       if(isEmpty(find_user)){
         console.log("들어왔다들어왔다")
         res.write("NO");
         res.end();
       }
       else{
        res.write("1");
        res.end();
       }


   }
   });

})


});

/*-----------------------------
 거리순 가게 추천 부분
--------------------------*/
app.post('/recommend', (req, res) => {
  req.on('data', (data)=>{
    inputData = JSON.parse(data);
  });
   req.on('end', ()=> {
    //모든 가게 찾기
    User.find({'store.name':{$ne:null}}, function(err, find_stores){
      if(err){
        //가게 찾기를 실패했을 때
        console.log("find failed");
      }
      if(find_stores != null)
      {
        //가게를 하나라도 찾았다면
        console.log("*find all store(recommend_part)*");
        //위치

        console.log(JSON.stringify(find_stores));
        //찾은 json 배열을 json 형태로 변환

        res.write(JSON.stringify(find_stores));
        res.end();
      }
   });
 });
});


var count = 0;
/*-----------------------------
  모든 가게 보기 부분
--------------------------*/
app.post('/allstore', (req, res) => {
  req.on('data', (data)=>{
    inputData = JSON.parse(data);
  });
   req.on('end', ()=> {
    //모든 가게 찾기
    User.find({'store.name':{$ne:null}}, function(err, find_stores){
      if(err){
        //가게 찾기를 실패했을 때
        console.log("find failed");
      }
      if(find_stores != null)
      {
        //가게를 하나라도 찾았다면
        console.log("*********찾음**********");
        console.log(JSON.stringify(find_stores));
        //찾은 json 배열을 json 형태로 변환

        res.write(JSON.stringify(find_stores));
        res.end();
      }
   });
 });
});


var count = 0;
/*-----------------------------
예약 요청 부분
--------------------------*/
app.post('/reservereq', (req, res) => {
   console.log('reservereq connect');

   req.on('data', (data)=>{
     inputData = JSON.parse(data);
   });

   req.on('end', ()=> {
    console.log("email : "+inputData.user_email);

    count++;//예약 요청 번호 매기기
  //var newreservationSchema = new ReservationSchema({
//    num:count,
//    state:inputData.state,
//   store:inputData.store_name,
//   purpose:inputData.purpose,
//   dateMonth:inputData.dateMonth,
//   dateDay:inputData.dateDay,
//   timeHour:inputData.timeHour,
//   timeMin:inputData.timeMin,
//   numberOfPeople: inputData.numberOfPeople,
//   });
   var update = {"$push":{reservation:{
     num:count,
     state:inputData.state,
    store:inputData.store_name,
    purpose:inputData.purpose,
    dateMonth:inputData.dateMonth,
    dateDay:inputData.dateDay,
    timeHour:inputData.timeHour,
    timeMin:inputData.timeMin,
    numberOfPeople: inputData.numberOfPeople,
    }}};
   User.findOneAndUpdate({email:inputData.user_email}, update, function(err, data){
     if(err){

     }
     else{
       console.log(inputData.store_name);
       console.log("reservation success");
     }
   });
   User.findOneAndUpdate({'store.name':inputData.store_name}, update, function(err, data) {
     //가게에약 update
     if(err) {

     }
     else {
       console.log(data);
       console.log("update storre");
     }
   });
})
   res.write("OK!");
   res.end();
});


/*-----------------------------
  예약 내역 보기 부분
--------------------------*/
app.post('/myreserve', (req, res) => {

  req.on('data', (data)=>{
    inputData = JSON.parse(data);
  });

   req.on('end', ()=> {
     console.log("email : "+inputData.user_email);

     // 예약 내역찾기
     // 일단 내가 한건가 확인
     /*var query = {$group:{
       {"$email":inputData.user_email},
       {"reservation.0":{"$exists": true}}
     }
   }*/

   User.aggregate([
   {
    $match:{email:inputData.user_email}
   }
],function(err,find_reserves){
   if(find_reserves){//user를 찾음
    if(find_reserves.reservation)
    console.log("이메일에 맞는 사람을 찾아버렸다")
    console.log(find_reserves);
      console.log(JSON.stringify(find_reserves));
    res.write(JSON.stringify(find_reserves));
    res.end();
   }
});
    /*User.find(query,function(err,find_reserves){
      if(err) {
        console.log("my reservation not found");
      }
      if(find_reserves != null)
      {
        // 예약 내역 하나라도 찾으면
        console.log("*********예약내역**********");
        console.log(find_reserves);
        console.log(JSON.stringify(find_reserves));
        //찾은 json 배열을 json 형태로 변환

        res.write(JSON.stringify(find_reserves));
        res.end();
    }
  });*/
     /*User.find({email:inputData.user_email}, function(err, find_my) {
       if(err) {
         console.log("my reservation not found");
       }
       // 내 예약 찾았으면
       if(find_my != null) {
         console.log("이메일까지는 찾음");
         console.log(find_my);
         var query = {$"reservation":{"$gt": 0}};
         //{ "departments.0": { "$exists": true } }

         find_my.find(query, function(err, find_reserves) {
           if(err){
             // 예약 내역 찾기 실패했을 때
             console.log("my reserve request find failed");
           }
           if(find_reserves != null)
           {
             // 예약 내역 하나라도 찾으면
             console.log("*********예약내역**********");
             console.log(find_reserves);
             console.log(JSON.stringify(find_reserves));
             //찾은 json 배열을 json 형태로 변환

             res.write(JSON.stringify(find_reserves));
             res.end();
         }
       });
     }

   });*/
 });
});

/*---------------------------

사용자 개인 정보 수정 부분

---------------------------*/
app.post('/modifyuser', (req, res) => {
  var email;
  var password;
  var phoneNum; //전화번호
  var year; //년
  var month; //월
  var day; //일
   console.log('***************개인정보수정********************');
   var result;
   req.on('data', (data)=>{
     inputData = JSON.parse(data);

   });

   req.on('end', ()=> {

      email=inputData.email; //이메일
      password=inputData.password; //비밀번호
      phoneNum=inputData.phoneNum; //전화번호
       year=inputData.year; //년
      month=inputData.month; //월
      day=inputData.day; //일

         //가게 등록자의 기존 가게 정보를 쿼리하기
       User.findOne({email:email},function(err, find_user){
        if(err){
          console.log("find failed");
        }
          console.log(find_user);
          find_user.password = password;
          find_user.phoneNum = phoneNum;
          //find_user.year = year;
          //find_user.month = month;
          //find_user.day = day;

          find_user.save(function(error,data){
          if(error){
               console.log(error);
           }else{

               console.log('Saved!')
               res.write("OK!");
               res.end();
           }
          });



      });

  /*newUser.find(function(error,data){
  if(error){
       console.log(error);
   }else{
       console.log('Saved!')
   }
 });*/

})


});

/*---------------------------

특정 가게의 대표메뉴, 가격 보내는 부분

---------------------------*/
app.post('/menuprice', (req, res) => {
  var email;
   console.log('******************대표메뉴, 가격들 보내줄 것임*************************');
   var result;
   req.on('data', (data)=>{
     inputData = JSON.parse(data);
     store_name = inputData.store_name; //안드로이드로부터 가게 이름 받기

   });

   req.on('end', ()=> {
    console.log(email);
    User.find({'store.name':store_name}, function(err, find_user){
     if(err){
       console.log("find failed");
     }
       console.log(find_user);
        console.log(inputData.store_name+"의 메뉴, 가격 정보 찾음");
        res.send(find_user); //가게 정보 전송
        res.end();




   });

})


});

/*-----------------------------
예약 요청 수락
state를 2로 바꾸면 수락
--------------------------*/
app.post('/acceptrespond', (req, res) => {
   console.log('acceptrespond connect');

   req.on('data', (data)=>{
     inputData = JSON.parse(data);
     num = inputData.num;
   });



   req.on('end', ()=> {
     console.log("**********예약번호************");
     console.log(num);//예약번호
   User.update({'reservation.num':num}, { $set: { "reservation.$.state" : 2 } }, { multi: true } , function(err, data) {
    //가게에약 update
    if(err)
      console.log("수락이 아안돼");
      else{
        console.log(data);
         console.log("예약 상태를 2(수락)으로 update 완료");
      }
   });


  });

});

/*-----------------------------
예약 요청 거절
state를 3로 바꾸면 거절
--------------------------*/
app.post('/rejectrespond', (req, res) => {
   console.log('rejectrespond connect');

   req.on('data', (data)=>{
     inputData = JSON.parse(data);
     num = inputData.num;
   });



   req.on('end', ()=> {
     console.log("**********예약번호************");
     console.log(num);//예약번호
   User.update({'reservation.num':num}, { $set: { "reservation.$.state" : 3 } }, { multi: true } , function(err, data) {
    //가게에약 update
    if(err)
      console.log("거절이 아안돼");
      else{
        console.log(data);
         console.log("예약 상태를 3(거절)으로 update 완료");
      }
   });
  });
});

var isEmpty = function(value)
{
  if( value == "" || value == null || value == undefined || ( value != null && typeof value == "object" && !Object.keys(value).length ) )
  {
    return true
   }else{
     return false
   } };



app.listen(3000, () => {
  console.log('Example app listening on port 3000!');
});

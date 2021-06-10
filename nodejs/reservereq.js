var count = 1;
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

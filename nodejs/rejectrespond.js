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

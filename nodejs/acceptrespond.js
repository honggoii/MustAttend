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

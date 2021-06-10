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
  });
});

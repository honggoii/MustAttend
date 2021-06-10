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
    User.find({email:email, password:password}, function(err, find_user){
      if(err){
        console.log("find failed");
      }
      if(find_user != null){
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

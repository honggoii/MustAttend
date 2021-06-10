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
      
    if(find_stores != null){
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

const app = require('express')();
const bodyParser = require('body-parser');
const jwt = require('jsonwebtoken');
const expressJWT = require('express-jwt');

const fs = require('fs');

app.use(bodyParser.json());

const secretKey = "abcdefg"


class User {
    constructor({id,tel,pass}) {
        this.id = id;
        this.tel = tel;
        this.pass = pass;
    }
}

const users = JSON.parse(fs.readFileSync('./account.json'));

app.post('/register', (req, res) => {
    const {id,tel,pass} = req.body;
    const user = new User({id,tel,pass});

    for (const u of users) {
        if (u.tel === tel) {
            res.status(401).send('User already exists');
            return;
        }
    }

    users.push(user);

    fs.writeFileSync('./account.json', JSON.stringify(users));
    res.status(200).send('User created');

});

app.post('/login', (req, res) => {
    const {id, pass} = req.body;

    for (const u of users) {
        if (u.id === id && u.pass === pass) {


            res
                .status(200)
                .json({
                    token: jwt.sign({id: u.id},secretKey,{expiresIn: '1h'})
                });
            return;
        }
    }

    res.status(401).send('Login failed');

});

app.get("/auth/token",(req,res)=>{
    const token = req.query.token
    try{
        const decoded = jwt.verify(token,secretKey)
        res.status(200)
            .json({
                data:decoded
            })
    }catch(e){
        console.error(e)
        res.status(401).send('Invalid token')
        return
    }
})

app.listen(3000, () => {
    console.log('Server is running');
})
<?php
class Database{
    private $conn;

    public function connect(){
        $this->conn = new mysqli('localhost','root', '', 'holidayapp');
        if($this->conn->connect_error){
            die("Connection failed " . $this->conn->connect_error);
        }
        return $this->conn;
    }

    public function disconnect(){
        if($this->conn){
            $this->conn->close();
        }
    }
}
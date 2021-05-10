<?php
class UserModel{
    private $conn;

    public function _construct(){
        $db = new Database();
        $this->conn = $db->connect();
    }

    public function login($account, $password){
        $query = "SELECT *
                FROM user
                WHERE (username = '$account' OR email = 'account' OR phone = '$account') AND password = '$password'";
        $result = $this->conn->query($query);
        $data = null;
        if($result->num_rows > 0){
            $row = $result->fecth_assoc();
            $data = array(
                'success' => true,
                'username' => $row['username'],
                'role' => $row['role']
            );
        }
        else
            $data = array('success' => false);
        return $data;
    }
}
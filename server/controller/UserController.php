<?php
class UserController extends BaseController{
    private $user_model;

    public function __construct() {
        $this->model("UserModel");
        $this->user_model = new UserModel();
    }

    public function login() {
        if($_SERVER['REQUEST_METHOD'] == 'POST'){
            $_POST = filter_input_array(INPUT_POST, FILTER_SANITIZE_STRING);

            $data = array(
                'account' => trim($_POST['account']),
                'password' => trim($_POST['password'])
            );

            $login_user = $this->user_model->login($data['account'], $data['password']);
            echo json_encode($login_user);
        }
    }

    public function detail() {
        if ($_SERVER['REQUEST_METHOD'] == 'GET') {
            $username = $_GET['username'];
            $user_detail = $this->user_model->detail($username);
            echo json_encode($user_detail);
        }
    }

    public function signup() {
        if ($_SERVER['REQUEST_METHOD'] == 'POST') {
            $_POST = filter_input_array(INPUT_POST, FILTER_SANITIZE_STRING);

            $data = array(
                'username' => trim($_POST['username']),
                'email' => trim($_POST['email']),
                'phone' => trim($_POST['phone']),
                'password' => trim($_POST['password']),
                'fullname' => trim($_POST['fullname'])
            );
            $signup_user = $this->user_model->signup(
                $data['username'],
                $data['email'],
                $data['phone'],
                $data['password'],
                $data['fullname']
            );
            echo json_encode($signup_user);
        }
    }

    public function update() {
        if ($_SERVER['REQUEST_METHOD'] == 'POST') {
            $_POST = filter_input_array(INPUT_POST, FILTER_SANITIZE_STRING);

            $data = array(
                'avatar' => trim($_POST['avatar']),
                'fullname' => trim($_POST['fullname']),
                'username' => trim($_POST['username']),
                'email' => trim($_POST['email']),
                'phone' => trim($_POST['phone']),
                'new_password' => trim($_POST['new_password'])
            );
            $update_user = $this->user_model->update(
                $data['avatar'],
                $data['fullname'],
                $data['username'],
                $data['email'],
                $data['phone'],
                $data['new_password']
            );
            echo json_encode($update_user);
        }
    }
}
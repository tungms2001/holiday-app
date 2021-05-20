<?php
class UserController extends BaseController{
    private UserModel $user_model;

    public function __construct() {
        $this->model("UserModel");
        $this->user_model = new UserModel();
    }

    public function login() {
        if($_SERVER['REQUEST_METHOD'] == 'POST'){
            $_POST = filter_input_array(INPUT_POST, FILTER_SANITIZE_STRING);

            $login_user = $this->user_model->login(
                trim($_POST['account']),
                trim($_POST['password'])
            );
            echo json_encode($login_user);
        }
    }

    public function get_detail() {
        if ($_SERVER['REQUEST_METHOD'] == 'GET') {
            $user_detail = $this->user_model->get_detail($_GET['username']);
            echo json_encode($user_detail);
        }
    }

    public function signup() {
        if ($_SERVER['REQUEST_METHOD'] == 'POST') {
            $_POST = filter_input_array(INPUT_POST, FILTER_SANITIZE_STRING);

            $signup_user = $this->user_model->signup(
                trim($_POST['username']),
                trim($_POST['email']),
                trim($_POST['phone']),
                trim($_POST['password']),
                trim($_POST['fullname'])
            );
            echo json_encode($signup_user);
        }
    }

    public function update() {
        if ($_SERVER['REQUEST_METHOD'] == 'POST') {
            $_POST = filter_input_array(INPUT_POST, FILTER_SANITIZE_STRING);

            $update_user = $this->user_model->update(
                trim($_POST['avatar']),
                trim($_POST['fullname']),
                trim($_POST['username']),
                trim($_POST['email']),
                trim($_POST['phone']),
                trim($_POST['new_password'])
            );
            echo json_encode($update_user);
        }
    }
}
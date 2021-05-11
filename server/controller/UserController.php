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
}
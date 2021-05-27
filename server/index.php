<?php
require 'controller/BaseController.php';
require 'model/Database.php';

$controller_name = ucfirst($_REQUEST['controller'] . 'Controller');//đối tượng
$action_name = strtolower($_REQUEST['action']);//phương thức

require "controller/${controller_name}.php";

$controller_object = new $controller_name();//biến được tạo để dùng trong mọi trường hợp dễ dàng
$controller_object->$action_name();
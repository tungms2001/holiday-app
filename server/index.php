<?php
require 'controller/BaseController.php';
require 'model/Database.php';

$controller_name = ucfirst($_REQUEST['controller'] . 'Controller');
$action_name = strtolower($_REQUEST['action']);

require "controller/${controller_name}.php";

$controller_object = new $controller_name();
$controller_object->$action_name();
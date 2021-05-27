<?php
class BaseController { //hổ trợ cho index
    const VIEW_FOLDER = 'view/';
    const MODEL_FOLDER = 'model/';

    final protected function view($view_path, array $data = []) {
        foreach ($data as $key => $value) {
            $$key = $value;
        }
        $view_path = self::VIEW_FOLDER . str_replace('.', '/', $view_path) . '.php';
        require $view_path;
    }
    final protected function model($model_path) {
        $model_path = self::MODEL_FOLDER . str_replace('.', '/', $model_path) . '.php';
        require $model_path;
    }
}
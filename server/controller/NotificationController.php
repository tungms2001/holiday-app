<?php
class NotificationController extends BaseController {
    private NotificationModel $notification_model;

    public function __construct()
    {
        $this->model('NotificationModel');
        $this->notification_model = new NotificationModel();
    }

    public function apply(){
        if ($_SERVER['REQUEST_METHOD'] == 'GET') {
            $apply = $this->notification_model->apply(
                intval($_GET['tour_id']),
                $_GET['sender'],
                $_GET['receiver']
            );
            echo json_encode($apply);
        }
    }

    public function load_all() {
        if ($_SERVER['REQUEST_METHOD'] == 'GET') {
            $notifications = $this->notification_model->load_all($_GET['username']);
            echo json_encode($notifications);
        }
    }
}
<?php
class NotificationController extends BaseController {
    private $notification_model;

    public function apply() {
        $this->model('NotificationModel');
        $this->notification_model = new NotificationModel();
        if ($_SERVER['REQUEST_METHOD'] == 'GET') {
            $tour_id = intval($_GET['tour_id']);
            $sender = $_GET['sender'];
            $receiver = $_GET['receiver'];
            $apply = $this->notification_model->apply($tour_id, $sender, $receiver);
            echo json_encode($apply);
        }
    }

    public function load_all() {
        $this->model('NotificationModel');
        $this->notification_model = new NotificationModel();
        if ($_SERVER['REQUEST_METHOD'] == 'GET') {
            $username = $_GET['username'];
            $notifications = $this->notification_model->load_all($username);
            echo json_encode($notifications);
        }
    }
}
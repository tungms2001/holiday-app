<?php
class TourController extends BaseController {
    private $tour_model;

    public function __construct() {
        $this->model('TourModel');
        $this->tour_model = new TourModel();
    }

    public function create() {
        if ($_SERVER['REQUEST_METHOD'] == 'POST') {
            $_POST = filter_input_array(INPUT_POST, FILTER_SANITIZE_STRING);

            $data = array(
                'creator' => trim($_POST['creator']),
                'tour_name' => trim($_POST['tour_name']),
                'type' => trim($_POST['type']),
                'status' => trim($_POST['status']),
                'departure' => trim($_POST['departure']),
                'destination' => trim($_POST['destination']),
                'during' => trim($_POST['during']),
                'members' => trim($_POST['members']),
                'note' => trim($_POST['note']),
                'image' => trim($_POST['image'])
            );

            $create_tour = $this->tour_model->create(
                $data['creator'],
                $data['tour_name'],
                $data['type'],
                $data['status'],
                $data['departure'],
                $data['destination'],
                $data['during'],
                $data['members'],
                $data['note'],
                $data['image']
            );

            echo json_encode($create_tour);
        }
    }

    public function load_all() {
        if ($_SERVER['REQUEST_METHOD'] == 'GET') {
            $tours = $this->tour_model->load_all();
            echo json_encode($tours);
        }
    }

    public function load_my_tours() {
        if ($_SERVER['REQUEST_METHOD'] == 'GET') {
            $username = $_GET['username'];
            $tours = $this->tour_model->load_my_tours($username);
            echo json_encode($tours);
        }
    }

    public function load_by_position() {
        if ($_SERVER['REQUEST_METHOD'] == 'GET') {
            $position = $_GET['position'];
            $tour = $this->tour_model->load_by_position($position);
            echo json_encode($tour);
        }
    }

    public function comment() {
        if ($_SERVER['REQUEST_METHOD'] == 'POST') {
            $_POST = filter_input_array(INPUT_POST, FILTER_SANITIZE_STRING);

            $data = array(
                'tour_id' => trim($_POST['tour_id']),
                'username' => trim($_POST['username']),
                'content' => trim($_POST['content'])
            );
            $comment = $this->tour_model->comment($data['tour_id'], $data['username'], $data['content']);
            echo $comment;
        }
    }

    public function load_comments() {
        if ($_SERVER['REQUEST_METHOD'] == 'GET') {
            $id = intval($_GET['id']);
            $comments = $this->tour_model->load_comments($id);
            echo json_encode($comments);
        }
    }
}
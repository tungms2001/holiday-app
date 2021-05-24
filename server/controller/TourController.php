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

            $create_tour = $this->tour_model->create(
                trim($_POST['creator']),
                trim($_POST['tour_name']),
                trim($_POST['type']),
                trim($_POST['status']),
                trim($_POST['departure']),
                trim($_POST['destination']),
                trim($_POST['during']),
                trim($_POST['members']),
                trim($_POST['note']),
                trim($_POST['image'])
            );
            echo json_encode($create_tour);
        }
    }

    public function update() {
        if ($_SERVER['REQUEST_METHOD'] == 'POST') {
            $_POST = filter_input_array(INPUT_POST, FILTER_SANITIZE_STRING);

            $update_tour = $this->tour_model->update(
                trim($_POST['id']),
                trim($_POST['tour_name']),
                trim($_POST['type']),
                trim($_POST['status']),
                trim($_POST['departure']),
                trim($_POST['destination']),
                trim($_POST['during']),
                trim($_POST['members']),
                trim($_POST['note']),
                trim($_POST['image'])
            );
            echo json_encode($update_tour);
        }
    }

    public function accept() {
        if ($_SERVER['REQUEST_METHOD'] == 'GET') {
            $accept_applying = $this->tour_model->accept(
                $_GET['username'],
                intval($_GET['id'])
            );
            return $accept_applying;
        }
    }

    public function load_all() {
        if ($_SERVER['REQUEST_METHOD'] == 'GET') {
            $tours = $this->tour_model->load_all();
            echo json_encode($tours);
        }
    }

    public function load_by_username() {
        if ($_SERVER['REQUEST_METHOD'] == 'GET') {
            $tours = $this->tour_model->load_by_username($_GET['username']);
            echo json_encode($tours);
        }
    }

    public function get_detail() {
        if ($_SERVER['REQUEST_METHOD'] == 'GET') {
            $tour = $this->tour_model->load_by_position(
                $_GET['position'],
                $_GET['keyword'],
                $_GET['username']
            );
            echo json_encode($tour);
        }
    }

    public function search() {
        if ($_SERVER['REQUEST_METHOD'] == 'POST') {
            $_POST = filter_input_array(INPUT_POST, FILTER_SANITIZE_STRING);
            $tours = $this->tour_model->search(trim($_POST['keyword']));
            echo json_encode($tours);
        }
    }
}
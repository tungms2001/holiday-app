<?php
class CommentController extends BaseController {
    private CommentModel $comment_model;

    public function __construct() {
        $this->model('CommentModel');
        $this->comment_model = new CommentModel();
    }

    public function create() {
        if ($_SERVER['REQUEST_METHOD'] == 'POST') {
            $_POST = filter_input_array(INPUT_POST, FILTER_SANITIZE_STRING);
            $comment = $this->comment_model->create(
                trim($_POST['tour_id']),
                trim($_POST['username']),
                trim($_POST['content'])
            );
            echo json_encode($comment);
        }
    }

    public function load_all() {
        if ($_SERVER['REQUEST_METHOD'] == 'GET') {
            $comments = $this->comment_model->load_all(intval($_GET['tour_id']));
            echo json_encode($comments);
        }
    }
}
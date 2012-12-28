<?php

/**
 * MsgController
 * 
 * @author
 * @version 
 */
require_once 'Zend/Controller/Action.php';

Zend_Loader::loadClass('MsgModel');

class MsgController extends Zend_Controller_Action {

    private $user;

    public function init() {
        $acl = Zend_Registry::get("acl");
        $this->user = $acl->getUser();
        // $this->_helper->layout->disableLayout();
        $this->_helper->viewRenderer->setNoRender();
    }

    public function sendAction() {
        $response = array();
        if ($this->getRequest()->isPost()) {
            $data = $this->getRequest()->getPost();
            $msg = new MsgModel ();
            $msg->insert(array(
                'senderid' => $this->user ['uid'],
                'rcverid' => $data ['rcvId'],
                'content' => $data ['content'],
                'type' => $data ['type'],
                'audiotime' => $data ['audiotime'],
                'time' => time(),
                'status' => 0
            ));
            $response ['errno'] = 0;
        } else {
            $response ['errno'] = 1;
        }
        echo json_encode($response);
    }

    public function newAction() {
        $response = array();
        $msg = new MsgModel ();
        $result = $msg->getNewMsg($this->user ['uid']);
        if ($result != null) {
            $count = 0;
            foreach ($result as $r) {
                array_push($response, array(
                    'type' => $r ['type'],
                    'sender' => $r ['name'],
                    'sendid' => $r ['senderid'],
                    'content' => $r ['content'],
                    'audiotime' => $r ['audiotime'],
                    'time' => $r ['time'],
                ));
                $count++;
            }
            $response ['errno'] = 0;
            $response ['count'] = $count;
        } else {
            $response ['errno'] = 1;
        }
        echo json_encode($response);
    }

}

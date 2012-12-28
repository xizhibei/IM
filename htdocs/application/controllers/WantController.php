<?php

/**
 * LocationController
 * 
 * @author
 * @version 
 */
require_once 'Zend/Controller/Action.php';

Zend_Loader::loadClass("WantModel");
Zend_Loader::loadClass("ActivityModel");

class WantController extends Zend_Controller_Action {

    private $user;

    public function init() {
        $acl = Zend_Registry::get("acl");
        $this->user = $acl->getUser();
        $this->_helper->viewRenderer->setNoRender();
    }

    public function indexAction() {
        // TODO Auto-generated LocationController::indexAction() default action
    }

    public function publishAction() {
        $response = array();
        if ($this->getRequest()->isPost()) {
            $data = $this->getRequest()->getPost();

            $starttime = new Zend_Date();
            $starttime->setHour($data['startH']);
            $starttime->setMinute($data['startM']);
            $starttime->setSecond(0);
            
            $endtime = new Zend_Date();
            $endtime->setHour($data['endH']);
            $endtime->setMinute($data['endM']);
            $endtime->setSecond(0);

            $want = new WantModel();
            $activity = new ActivityModel();
            $acid = $activity->GetActivityId($data['name']);
            
            $insertData = array(
                'acid' => $acid,
                'expiretime' => $endtime->toValue(),
                'starttime' => $starttime->toValue(),
                'sextype' => $data['sextype'],
                'uid' => $this->user['uid'],
                'status' => WantModel::Normal,
            );
            $want->insert($insertData);
            $response['errno'] = 0;
        } else {
            $response['errno'] = 2;
        }
        
        
        echo json_encode($response);
    }

}

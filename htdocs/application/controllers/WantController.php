<?php

/**
 * LocationController
 * 
 * @author
 * @version 
 */
require_once 'Zend/Controller/Action.php';

Zend_Loader::loadClass("UserModel");
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

    public function historyAction() {
        if (isset($this->user['uid']))
            $uid = $this->user['uid'];
        if ($this->getRequest()->isPost()) {
            $data = $this->getRequest()->getPost();
            $uid = $data['uid'];
        }
        $want = new WantModel();
        $response = array();
        $result = $want->getHistory($uid);
        if ($result != null) {
            $response['errno'] = 0;
            $response['count'] = count($result);
            foreach ($result as $r) {
                array_push($response, array(
                    'a_name' => $r['a_name'],
                    'expiretime' => $r['expiretime'],
                    'starttime' => $r['starttime'],
                    'sextype' => $r['sextype'],
                    'd_updatetime' => $r['d_updatetime'],
                    'detail' => $r['d_detail'],
                ));
            }            
        }else{
            $response['errno'] = 1;
        }
        echo json_encode($response);
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
                'd_detail' => 'None',
                'd_updatetime' => time(),
                'd_status' => WantModel::Normal,
            );
            $id = $want->insert($insertData);

            $user = new UserModel();
            $user->update(
                    array('wandid' => $id), "uid = " . $this->user['uid']
            );
            $response['did'] = $id;
            $response['errno'] = 0;
        } else {
            $response['errno'] = 2;
        }
        echo json_encode($response);
    }

}

package com.sellproducts.thiennt.sellstoreSever.Service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.sellproducts.thiennt.sellstoreSever.Common.Common;
import com.sellproducts.thiennt.sellstoreSever.model.Token;

    public class MyFirebaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String tokenfresh = FirebaseInstanceId.getInstance().getToken();
        /////////////////
            uptoken(tokenfresh);

    }

    private void uptoken(String tokenfresh) {

        FirebaseDatabase db =FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token token = new Token(tokenfresh,true); //fasle, vi token gui tu clien app
        tokens.child(Common.currentUser.getPhone()).setValue(token);
    }
}

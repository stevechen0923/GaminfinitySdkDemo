//
//  GaminfinitySDK.h
//  GaminfinitySDK
//
//  Created by funtown on 2014/8/12.
//  Copyright (c) 2014年 App Developer Center. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol EventHandler
@required
-(void) onGetAccountId:(int)result AccountId:(NSString*) accountId;
@end

@interface GaminfinitySDK : NSObject
@property (nonatomic, weak) id <EventHandler> delegate;
-(void) getAccountId:(NSString*)accessToken ServerUrl:(NSString*)serverurl;

@end

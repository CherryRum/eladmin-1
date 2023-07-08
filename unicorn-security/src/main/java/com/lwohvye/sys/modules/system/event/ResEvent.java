/*
 *    Copyright (c) 2022-2023.  lWoHvYe(Hongyan Wang)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.lwohvye.sys.modules.system.event;

import com.lwohvye.api.modules.system.domain.Resource;

public class ResEvent extends BaseEvent<Resource> {
    /**
     * @param source    最初触发该事件的对象
     * @param eventData 该类型事件携带的信息
     */
    public ResEvent(Object source, Resource eventData) {
        super(source, eventData);
    }
}

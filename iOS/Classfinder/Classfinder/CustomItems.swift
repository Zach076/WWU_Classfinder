//
//  CustomItems.swift
//  Classfinder
//
//  Created by Guest on 10/17/19.
//  Copyright © 2019 Guest. All rights reserved.
//

import UIKit

final class Schedule {
    public var name:String
    public var classes: [Class]
    init(name:String, classes:[Class]) {
        self.name = name
        self.classes = classes
    }
}

final class Class {
    public var name:String = ""
    init(){
        self.name = ""
    }
}

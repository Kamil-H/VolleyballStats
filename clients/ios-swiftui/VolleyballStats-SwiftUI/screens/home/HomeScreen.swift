//
//  HomeScreen.swift
//  VolleyballStats-SwiftUI
//
//  Created by Kamil Halko on 21/12/2023.
//

import SwiftUI
import shared

struct HomeScreen: View {
    
    private let scope: Scope
    private let homePresenter: HomePresenter
    
    init(presentersFactory: PresentersFactory) {
        self.scope = presentersFactory.createScope()
        self.homePresenter = presentersFactory.createHomePresenter(scope: scope)
    }
    
    var body: some View {
        Color.red
    }
}

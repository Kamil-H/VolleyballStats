//
//  StatsScreen.swift
//  VolleyballStats-SwiftUI
//
//  Created by Kamil Halko on 02/01/2024.
//

import Foundation
import SwiftUI
import shared

struct StatsScreen: View {
    
    private let scope: Scope
    private let statsPresenter: StatsPresenter
    
    init(presentersFactory: PresentersFactory, screen: ScreenStats) {
        self.scope = presentersFactory.createScope()
        self.statsPresenter = presentersFactory.createStatsPresenter(scope: scope, screen: screen)
    }
    
    var body: some View {
        Button("BUTTON") {
            print("BUTTON")
        }
    }
}

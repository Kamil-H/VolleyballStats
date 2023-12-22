//
//  Icon.swift
//  VolleyballStats-SwiftUI
//
//  Created by Kamil Halko on 22/12/2023.
//

import SwiftUI
import shared

struct IconImage: View {
    
    private let icon: Icon
    
    init(icon: Icon) {
        self.icon = icon
    }
    
    var body: some View {
        Image(systemName: getIconName(icon: icon))
            .font(.system(size: 20, weight: .light))
    }
}

func getIconName(icon: Icon) -> String {
    switch icon {
    case .scoreboard:
        "sportscourt" // subject to change
    case .tune:
        "slider.horizontal.3"
    case .person:
        "person"
    case .groups:
        "person.3"
    case .refresh:
        "arrow.clockwise"
    case .arrowBack:
        "arrow.left"
    }
}

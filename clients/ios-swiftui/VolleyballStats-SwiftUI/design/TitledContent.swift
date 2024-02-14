//
//  TitledContent.swift
//  VolleyballStats-SwiftUI
//
//  Created by Kamil Halko on 17/01/2024.
//

import SwiftUI

struct TitledContent<Content: View>: View {
    let title: String?
    let contentMargin: Bool
    let content: () -> Content

    var body: some View {
        VStack {
            let margin = Dimens.marginMedium
            if let title = title {
                Text(title)
                    .font(.largeTitle)
                    .padding(.horizontal, margin)
                    .padding(.top, margin)
            }
            Spacer(minLength: Dimens.marginSmall)
            content()
                .padding(contentMargin ? .horizontal : [])
            Spacer(minLength: Dimens.marginSmall)
        }
    }
}

//
//  Banner.swift
//  VolleyballStats-SwiftUI
//
//  Created by Kamil Halko on 04/01/2024.
//

import Foundation
import SwiftUI
import shared

struct BannerModifier: ViewModifier {

    @State var message: Message?
    let onDismiss: () -> Void
    let onButtonClicked: () -> Void
    
    @State private var task: DispatchWorkItem?

    func body(content: Content) -> some View {
        ZStack {
            if let message {
                VStack {
                    HStack {
                        VStack(alignment: .leading, spacing: 2) {
                            Text(message.text).bold()
                        }
                        Spacer()
                        if let buttonText = message.buttonText {
                            Button(buttonText) {
                                onButtonClicked()
                            }
                        }
                    }
                    .foregroundColor(Color.white)
                    .padding(Dimens.marginMedium)
                    .background(Color.secondary)
                    .cornerRadius(Dimens.cornerSmall)
                    .shadow(radius: 8)
                    Spacer()
                }
                .padding()
                .animation(.easeInOut(duration: 0.5))
                .transition(AnyTransition.move(edge: .bottom).combined(with: .opacity))
                .onAppear {
                    self.task = DispatchWorkItem {
                        onDismiss()
                    }
                    DispatchQueue.main.asyncAfter(deadline: .now() + 4, execute: self.task!)
                }
                .onDisappear {
                    self.task?.cancel()
                }
            }
            content
        }
    }
}

extension View {
    func banner(
        message: Message?,
        onDismiss: @escaping () -> Void,
        onButtonClicked: @escaping () -> Void
    ) -> some View {
        self.modifier(
            BannerModifier(
                message: message,
                onDismiss: onDismiss,
                onButtonClicked: onButtonClicked
            )
        )
    }
}


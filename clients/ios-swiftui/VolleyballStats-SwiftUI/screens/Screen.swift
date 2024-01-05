//
//  ScreenSkeleton.swift
//  VolleyballStats-SwiftUI
//
//  Created by Kamil Halko on 04/01/2024.
//

import Foundation
import SwiftUI
import shared

struct Screen<ScreenStateType: ScreenState, Content: View>: View {
    
    private let stateFlow: SkieSwiftStateFlow<ScreenStateType>
    private let scope: Scope
    private let onFabButtonClicked: () -> Void
    private let onActionButtonClicked: () -> Void
    private let onMessageButtonClicked: () -> Void
    private let onMessageDismissed: () -> Void
    private var content: (ScreenStateType) -> Content
    
    @State private var state: ScreenStateType
    
    init(
        stateFlow: SkieSwiftStateFlow<ScreenStateType>,
        scope: Scope,
        onFabButtonClicked: @escaping () -> Void = { },
        onActionButtonClicked: @escaping () -> Void = { },
        onMessageButtonClicked: @escaping () -> Void = { },
        onMessageDismissed: @escaping () -> Void = { },
        content: @escaping (ScreenStateType) -> Content
    ) {
        self.stateFlow = stateFlow
        self.scope = scope
        self.onFabButtonClicked = onFabButtonClicked
        self.onActionButtonClicked = onActionButtonClicked
        self.onMessageButtonClicked = onMessageButtonClicked
        self.onMessageDismissed = onMessageDismissed
        self.content = content
        self.state = stateFlow.value
    }
    
    var body: some View {
        ScreenView(
            state: state,
            onFabButtonClicked: onFabButtonClicked,
            onActionButtonClicked: onActionButtonClicked,
            onMessageButtonClicked: onMessageButtonClicked,
            onMessageDismissed: onMessageDismissed
        ) {
            content(state)
        }.task {
            await observeState()
        }.onDisappear {
            scope.cancel()
        }
    }
    
    @MainActor
    private func observeState() async {
        for await state in stateFlow {
            self.state = state
        }
    }
}

private struct ScreenView<Content: View>: View {
    
    let state: ScreenState
    let onFabButtonClicked: () -> Void
    let onActionButtonClicked: () -> Void
    let onMessageButtonClicked: () -> Void
    let onMessageDismissed: () -> Void
    var content: () -> Content
    
    var body: some View {
        NavigationView {
            VStack {
                if state.loadingState.showFullScreenLoading {
                    FullScreenLoadingView(loadingState: state.loadingState)
                } else {
                    ScreenContent(
                        state: state,
                        onMessageButtonClicked: onMessageButtonClicked,
                        onMessageDismissed: onMessageDismissed
                    ) {
                        content()
                    }
                }
            }
            .navigationTitle(state.topBarState.title ?? "")
            .navigationBarTitleDisplayMode(state.topBarState.showToolbar ? .automatic : .inline)
            .toolbar {
                ToolbarItemGroup(placement: .topBarTrailing) {
                    if let actionButtonIcon = state.actionButton.icon, state.actionButton.show {
                        Button(action: { onFabButtonClicked() }) {
                            Label("", systemImage: getIconName(icon: actionButtonIcon))
                        }
                    }
                    if let actionButtonIcon = state.topBarState.actionButtonIcon {
                        Button(action: { onActionButtonClicked() }) {
                            Label("", systemImage: getIconName(icon: actionButtonIcon))
                        }
                    }
                }
            }
        }
    }
}

private struct ScreenContent<Content: View>: View {
    
    let state: ScreenState
    let onMessageButtonClicked: () -> Void
    let onMessageDismissed: () -> Void
    var content: () -> Content

    var body: some View {
        VStack {
            if !state.topBarState.showToolbar {
                Spacer()
            }
            ZStack(alignment: .topLeading) {
                content()
                    .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
                if let linearProgressBar = state.loadingState.linearProgressBar {
                    LinearProgressView(linearProgressBar: linearProgressBar)
                }
            }.banner(
                message: state.message,
                onDismiss: onMessageDismissed,
                onButtonClicked: onMessageButtonClicked
            )
        }
    }
}

private struct FullScreenLoadingView: View {
    
    let loadingState: LoadingState
    
    var body: some View {
        VStack {
            if let linearProgressBar = loadingState.linearProgressBar {
                LinearProgressView(linearProgressBar: linearProgressBar)
            } else {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle())
            }
            if let text = loadingState.text {
                Text(text)
            }
        }
    }
}

private struct LinearProgressView: View {
    
    let linearProgressBar: LinearProgressBar
    
    init(linearProgressBar: LinearProgressBar) {
        self.linearProgressBar = linearProgressBar
    }
    
    var body: some View {
        switch onEnum(of: linearProgressBar) {
        case .indefinite:
            IndeterminateProgressView()
        case .progress(let value):
            ProgressView(value: value.value, total: 100.0)
        }
    }
}

struct ScreenView_Previews: PreviewProvider {
    
    static var previews: some View {
        ScreenView(
            state: HomeState(
                matches: [],
                scrollToItem: nil,
                itemToSnapTo: 0,
                onRefreshButtonClicked: { },
                onScrolledToItem: { _ in },
                loadingState: LoadingState(
                    text: "String",
                    showFullScreenLoading: false,
                    linearProgressBar: LinearProgressBarIndefinite()
                ),
                topBarState: TopBarState(
                    title: "Matches",
                    showToolbar: true,
                    background: .default,
                    navigationButtonIcon: nil,
                    actionButtonIcon: .refresh
                ),
                actionButton: ActionButton(
                    show: true,
                    icon: .tune
                ),
                message: Message(text: "text", buttonText: "button"),
                colorAccent: .default
            ),
            onFabButtonClicked: {},
            onActionButtonClicked: {},
            onMessageButtonClicked: {},
            onMessageDismissed: {}
        ) {
            VStack(alignment: .leading) {
                Text("Title")
                    .font(.title)
                Text("Content")
                    .font(.body)
            }
        }
    }
}
